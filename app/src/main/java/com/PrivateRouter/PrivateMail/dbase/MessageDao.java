package com.PrivateRouter.PrivateMail.dbase;

import android.arch.paging.DataSource;
import android.arch.persistence.db.SupportSQLiteQuery;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.RawQuery;
import android.arch.persistence.room.Update;

import com.PrivateRouter.PrivateMail.model.Contact;
import com.PrivateRouter.PrivateMail.model.ContactBase;
import com.PrivateRouter.PrivateMail.model.FolderHash;
import com.PrivateRouter.PrivateMail.model.Message;
import com.PrivateRouter.PrivateMail.model.MessageBase;
import com.PrivateRouter.PrivateMail.model.StorageCTag;
import com.PrivateRouter.PrivateMail.model.TempMessageIds;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.reactivex.Single;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface MessageDao {


    @Query("SELECT * FROM messages WHERE parentUid = 0 and Folder =:folder ORDER BY uid DESC")
    DataSource.Factory<Integer, Message> getAllFactory(String folder);

    @Query("SELECT * FROM messages WHERE Folder =:folder and " +
            "((Subject LIKE :filter) or (plain LIKE :filter) or (attachmentsattachments LIKE :filter) " +
            "or (toemails LIKE :filter) or (ccemails LIKE :filter) or (fromemails LIKE :filter) or (bccemails LIKE :filter))" +
            " ORDER BY uid DESC")
    DataSource.Factory<Integer, Message> getAllFilterFactory(String folder, String filter);

    @Query("SELECT * FROM messages WHERE parentUid = 0 and Folder =:folder and " +
            "(fromemails  LIKE :filter) " +
            " ORDER BY uid DESC")
    DataSource.Factory<Integer, Message> getAllFilterEmailFactory(String folder, String filter);


    @Query("SELECT * FROM messages WHERE parentUid = 0 and Folder =:folder and isFlagged ORDER BY uid DESC")
    DataSource.Factory<Integer, Message> getAllFactoryStarredOnly(String folder);

    @Query("SELECT * FROM messages WHERE Folder =:folder and isFlagged and " +
            "((Subject LIKE :filter) or (plain LIKE :filter) or (attachmentsattachments LIKE :filter) " +
            "or (toemails LIKE :filter) or (ccemails LIKE :filter) or (fromemails LIKE :filter) or (bccemails LIKE :filter))" +
            " ORDER BY uid DESC")
    DataSource.Factory<Integer, Message> getAllFilterFactoryStarredOnly(String folder, String filter);

    @Query("SELECT * FROM messages WHERE parentUid = 0 and Folder =:folder and isFlagged and " +
            "(fromemails  LIKE :filter) " +
            " ORDER BY uid DESC")
    DataSource.Factory<Integer, Message> getAllFilterEmailFactoryStarredOnly(String folder, String filter);



    @Query("SELECT * FROM messages WHERE parentUid = :parentUid and Folder =:folder ORDER BY uid DESC")
    List<Message> getAllThreadsMessages(String folder, int parentUid);


    @Query("SELECT * FROM messages WHERE messageId = :id")
    Message getById(String id);


    @Query("SELECT * FROM messages WHERE Folder =:folder and Uid = :uid")
    Message getByUid(String folder, Integer uid);

    @Query("DELETE FROM messages WHERE (Folder =:folder) and  " +
            "( uid IN (:uids))" )
    void deletedFromList(String folder, List<Integer> uids );


    @Query("SELECT * FROM messages WHERE (uid) and (Folder =:folder) and  (uid >= :minUI) and  (uid <= :maxUI) and " +
            "( uid NOT IN (:uids))" )
    List<Message> selectNotInList(String folder, int minUI, int maxUI, List<Integer> uids );

// uid, isFlagged, isAnswered, isSeen, isForwarded, isDeleted, isDraft, isRecent, parentUid, getThreadUidsList
    @Query("SELECT * FROM messages " +
             "WHERE (Folder =:folder) ORDER BY uid")
    List<MessageBase> selectMessageBase(String folder);

    @Query("UPDATE messages SET isFlagged = :isFlagged, isAnswered = :isAnswered, isSeen = :isSeen, isForwarded = :isForwarded," +
            "isDeleted = :isDeleted, isDraft = :isDraft, isRecent = :isRecent, parentUid  = :parentUid, threadUidsList = :threadUidsList   " +
            "WHERE (Folder =:folder) and (uid = :uid)")
    void updateMessageFlags(String folder, int uid, boolean isFlagged, boolean isAnswered, boolean isSeen,
                            boolean isForwarded, boolean isDeleted, boolean isDraft, boolean isRecent,
                            int parentUid, String threadUidsList);

    @Query("UPDATE messages SET folder = :folderTo WHERE (Folder =:folder) and (uid = :uid)")
    void updateMessageFolder(int uid,  String folder, String folderTo);



    @Insert(onConflict = REPLACE)
    Long[] insert(List<Message> messages);

    @Update
    void update(Message message);

    @Delete
    void delete(Message message);

    @Insert(onConflict = REPLACE)
    Long insertFolderHash(FolderHash folderHash);


    @Query("SELECT * FROM folder_hash WHERE folderName =:folder")
    FolderHash getFolderHash(String folder);


    @Query("DELETE FROM folder_hash" )
    void deletedFolderHashes( );


    @Query("SELECT * FROM contacts WHERE (Storage =:storage)")
    DataSource.Factory<Integer, Contact> getAllContactsInStorage(String storage);

    @Query("SELECT * FROM contacts WHERE Storage =:storage and " +
            "(FullName LIKE :filter or ViewEmail LIKE :filter )  ")
    DataSource.Factory<Integer, Contact> getAllFiltredContactsInStorage(String storage, String filter );





    @Query("SELECT * FROM contacts WHERE (groupUUIDs LIKE :groupID)")
    DataSource.Factory<Integer, Contact> getAllContactsInGroup(String groupID);

    @Query("SELECT * FROM contacts WHERE (groupUUIDs LIKE :groupID) and " +
            "(FullName LIKE :filter or ViewEmail LIKE :filter )  ")
    DataSource.Factory<Integer, Contact> getAllFiltredContactsInGroup(String groupID, String filter );



    @Query("SELECT * FROM contacts WHERE   UUID = :uuid")
    Contact getContactByUuid( String uuid);

    @Query("SELECT UUID, eTag FROM contacts WHERE Storage = :storage")
    List<ContactBase> getStorageContactsUids(String storage);

    @Insert(onConflict = REPLACE)
    Long insertContact(Contact contact);

    @Query("DELETE FROM contacts Where UUID IN (:UUIDs)")
    public void clearOldContacts(  List<String> UUIDs);


    @Insert(onConflict = REPLACE)
    Long insertStorageCTag(StorageCTag storageCTag);


    @Query("SELECT * FROM storage_c_tag WHERE storage =:storage")
    StorageCTag getStorageCTag(String storage);


    @Query("SELECT ids FROM temp_message_ids ")
    Long[] getMessageTempIds( );


    @Insert(onConflict = REPLACE)
    Long insertTempMessageId(TempMessageIds tempMessageIds);


    @Query("DELETE FROM temp_message_ids")
    void removeTempMessagesIds();


}
