package com.PrivateRouter.PrivateMail.network.logics;

import android.text.TextUtils;
import android.util.Log;

import com.PrivateRouter.PrivateMail.PrivateMailApplication;
import com.PrivateRouter.PrivateMail.dbase.AppDatabase;
import com.PrivateRouter.PrivateMail.dbase.ArrayIntegerConverter;
import com.PrivateRouter.PrivateMail.dbase.MessageDao;
import com.PrivateRouter.PrivateMail.model.InternalLists;
import com.PrivateRouter.PrivateMail.model.Message;
import com.PrivateRouter.PrivateMail.model.MessageBase;
import com.PrivateRouter.PrivateMail.model.TempMessageIds;
import com.PrivateRouter.PrivateMail.model.errors.ErrorType;
import com.PrivateRouter.PrivateMail.model.errors.OnErrorInterface;
import com.PrivateRouter.PrivateMail.network.requests.CallGetMessagesBodies;
import com.PrivateRouter.PrivateMail.network.requests.CallGetMessagesBase;
import com.PrivateRouter.PrivateMail.view.utils.Logger;
import com.PrivateRouter.PrivateMail.view.utils.MessageUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import androidx.annotation.NonNull;

public class LoadMessageLogic   implements OnErrorInterface {



    public static final String TAG = "LoadMessageLogic";
    private static final int MaxBodyMessageForLoad = 50;
    private static final int MaxBodyMessageForDelete = 500;


    private String folder;

    private List<MessageBase> cachedList;
    private List<MessageBase> serverList;



    private InternalLists<MessageBase> uidsForLoadingMain = new InternalLists<MessageBase>(MaxBodyMessageForLoad);
    private InternalLists<MessageBase> uidsForLoadingThreads = new InternalLists<MessageBase>(MaxBodyMessageForLoad);
    private InternalLists<Integer> uidsForDelete = new InternalLists<Integer>(MaxBodyMessageForDelete);

    LoadMessageAnswer loadMessageAnswer = new LoadMessageAnswer();
    private boolean saveToTempCache = false;
    private int iteration = 0;

    public void setIteration(int iteration) {
        this.iteration = iteration;
    }

    public void setSaveToTempCache(boolean value) {
        this.saveToTempCache = value;
    }

    public class LoadMessageAnswer {
        boolean success;
        boolean haveNewValue;
        ErrorType errorType = ErrorType.UNKNOWN;
        int errorCode;
    }

    public  LoadMessageLogic(@NonNull String folder) {
        this.folder = folder;
    }

    public LoadMessageAnswer load() {
        boolean success = process();
        loadMessageAnswer.success = success;
        return  loadMessageAnswer;
    }

    public boolean process() {
        Logger.i(TAG, "start loading for folder "+folder);
        Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
        boolean success;

        success = loadServerUidList();
        if (!success || isCancelled() || serverList == null) return false;

        loadCurrentsUids();
        if ( isCancelled()) return false;


        handleChanges();
        if ( isCancelled()) return false;

        removeDeletedFromCache();
        if ( isCancelled()) return false;

        success = loadMessagesBody();
        if ( !success || isCancelled() ) return false;



        Logger.i(TAG, "complete loading for folder "+folder);

        return true;
    }



    private boolean isCancelled() {
        return false;
    }


    private void handleChanges() {
        try {
            int serverCount = serverList.size();


            int lastIndexInServerList = 0;

            for (int i = 0; i < serverCount; i++) {
                MessageBase serverItem = serverList.get(i);

                int index = findInListLessThanUids(cachedList, serverItem.getUid(), 0);
                if (index == -1  ) { //|| serverItem.getParentUid() > 0
                    addForLoading( serverItem );
                }
                else {
                    MessageBase cashedItem = cachedList.get(index);

                    cachedList.remove(index);

                    lastIndexInServerList = index;

                    if (!serverItem.equals(cashedItem))
                        updateMessageInCash(serverItem);
                }


            }

            for (MessageBase messageBase : cachedList) {
                addForDelete(messageBase);
            }


        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void updateMessageInCash(MessageBase message) {

        AppDatabase database = PrivateMailApplication.getInstance().getDatabase();
        database.messageDao().updateMessageFlags(folder, message.getUid(), message.isFlagged(), message.isAnswered(), message.isSeen(),
                message.isForwarded(), message.isDeleted(), message.isDraft(), message.isRecent(),
                message.getParentUid(), ArrayIntegerConverter.fromArrayList(message.getThreadUidsList() )  );

    }

    private int findInListLessThanUids(List<MessageBase> list, int uid, int start) {
        for (int i = start; i< list.size(); i++) {
            if (list.get(i).getUid() == uid )
                return i;
            else if ( list.get(i).getUid() > uid  ) {
                return -1;
            }
        }
        return -1;
    }

    private boolean loadServerUidList() {
        long currentTime = System.currentTimeMillis();
        Logger.d(TAG, "loadServerUidList " +currentTime  );

        CallGetMessagesBase callGetMessages = new CallGetMessagesBase();
        callGetMessages.setFolder(folder);
        callGetMessages.setIteration(iteration);
        List<MessageBase> list = callGetMessages.syncStart(this);


        if (list!=null) {
            serverList = addThreadsUIDs(list);
        }


        Logger.v(TAG, "loadServerUidList complete in "+ (System.currentTimeMillis()-currentTime)+ " ms");
        return serverList != null;
    }

    private List<MessageBase> addThreadsUIDs(List<MessageBase> list) {

        List<MessageBase> fullList = new ArrayList<>(list.size() );
        fullList.addAll(list);
        for (MessageBase messageBase : list) {
            if (messageBase.getThread()!=null) {
                ArrayList<Integer> uids = new ArrayList<>(messageBase.getThread().size());
                for (MessageBase threadsMessageBase : messageBase.getThread()) {
                    uids.add(threadsMessageBase.getUid() );
                    threadsMessageBase.setParentUid( messageBase.getUid() );
                    fullList.add( threadsMessageBase );
                }
                messageBase.setThreadUidsList(uids);
            }
        }

        return fullList;
    }

    private void loadCurrentsUids() {
        long currentTime = System.currentTimeMillis();
        Logger.v(TAG, "loadCurrentsUids " +currentTime  );

        AppDatabase database = PrivateMailApplication.getInstance().getDatabase();
        cachedList = database.messageDao().selectMessageBase(folder);


        Logger.v(TAG, "loadCurrentsUids complete in "+ (System.currentTimeMillis()-currentTime)+ " ms  size =" + cachedList .size() );
    }


    private void addForLoading(MessageBase messageBase) {
        String str = (messageBase.getParentUid()==0)?"": " parentID = "+messageBase.getParentUid();
        Logger.v(TAG, "addForLoading "+ messageBase.getUid() + str);

        loadMessageAnswer.haveNewValue = true;
        if (messageBase.getParentUid()==0)
            uidsForLoadingMain.addElement(messageBase);
        else
            uidsForLoadingThreads.addElement(messageBase);
    }

    private void addForDelete(MessageBase messageBase) {
        Logger.v(TAG, "addForDelete "+messageBase.getUid());
        uidsForDelete.addElement(messageBase.getUid());
    }


    private void removeDeletedFromCache() {

        Logger.d(TAG, "removeDeletedFromCache cachesUidList "  );
        AppDatabase database = PrivateMailApplication.getInstance().getDatabase();
        MessageDao messageDao = database.messageDao();

        for (List<Integer> uids: uidsForDelete) {
            if (isCancelled()) return;

            messageDao.deletedFromList(folder, uids);
            Logger.v(TAG, "removeDeletedFromCache delete = "+ uids.size() );
        }

        Logger.v(TAG, "removeDeletedFromCache complete");
    }


    private boolean loadMessagesBody() {
        boolean success = loadMessagesBody(uidsForLoadingThreads);
        if ( !success || isCancelled() ) return false;

        success = loadMessagesBody(uidsForLoadingMain);
        if ( !success || isCancelled() ) return false;

        return success;
    }

    private boolean loadMessagesBody(InternalLists<MessageBase> internalLists) {
        Logger.d(TAG, "loadMessagesBody "+folder);

        boolean hasSkipped = false;


        for (int index = internalLists.size()-1; index >=0 ;  index--) {
            ArrayList<MessageBase> list = internalLists.get(index);

            if (isCancelled()) return false;

            Logger.v(TAG, "loadMessagesBody folder= "+folder +" list=" + index);
            ArrayList<Message> loadedMessage = loadMessagePatchBody(list);
            if (loadedMessage != null) {
                performThreadStruct(loadedMessage, list);
                cutLongHtml(loadedMessage);
                convertHtmlToPlain(loadedMessage);
                addToDBase(loadedMessage);
            }
            else {
                Logger.i(TAG, "loadMessagesBody "+ index+ " failed");
                if (hasSkipped)
                    return false;
                hasSkipped = true;
            }
          //  index++;
        }
        Logger.d(TAG, "loadMessagesBody complete");
        return !hasSkipped;
    }

    private void performThreadStruct(ArrayList<Message> loadedMessage, ArrayList<MessageBase> list) {
        for (int i = 0; i < loadedMessage.size(); i++) {
            Message message = loadedMessage.get(i);

            MessageBase messageBase = findInList(list, message.getUid(), i );
            if (messageBase!=null) {
                message.setParentUid( messageBase.getParentUid() );
                message.setThreadUidsList( messageBase.getThreadUidsList());
            }
        }
    }

    private MessageBase findInList(List<MessageBase> list, int uid, int start) {
        if (start>=0 && start<list.size() && list.get(start).getUid()==uid )
            return list.get(start);

        for (int i = 0; i< list.size(); i++) {
            if (list.get(i).getUid() == uid ) {
                return list.get(i);
            }
        }
        return null;
    }


    private void cutLongHtml(ArrayList<Message> loadedMessage) {
        for (Message message : loadedMessage) {
            int max = 1024*1024;
            if ( !TextUtils.isEmpty(message.getHtml() )   && message.getHtml().length()> max ) {
                String cutHtml = message.getHtml().substring(0, max);
                message.setHtml( cutHtml);
            }
        }
    }

    private void convertHtmlToPlain(ArrayList<Message> loadedMessage) {
        for (Message message : loadedMessage) {
            if ( TextUtils.isEmpty(message.getPlain() ) ) {
                String plain = MessageUtils.convertToPlain(message.getHtml());
                message.setPlain( plain);
            }
        }
    }

    private ArrayList<Message>  loadMessagePatchBody (ArrayList<MessageBase> uids) {
        CallGetMessagesBodies callGetMessagesBodies = new CallGetMessagesBodies();
        callGetMessagesBodies.setFolder(folder);
        callGetMessagesBodies.setUids(uids);
        return callGetMessagesBodies.syncStart();
    }

    private void addToDBase(ArrayList<Message> messages) {
        AppDatabase database = PrivateMailApplication.getInstance().getDatabase();
        Long [] newIds = database.messageDao().insert(messages);

        if (saveToTempCache) {
            for (Long id: newIds) {
                TempMessageIds tempMessageIds = new TempMessageIds();
                tempMessageIds.setIds(id);
                database.messageDao().insertTempMessageId(tempMessageIds);
            }

        }
    }


    @Override
    public void onError(ErrorType errorType, int errorCode) {
        this.loadMessageAnswer.errorType = errorType;
        this.loadMessageAnswer.errorCode = errorCode;
        this.loadMessageAnswer.success = false;
    }
}
