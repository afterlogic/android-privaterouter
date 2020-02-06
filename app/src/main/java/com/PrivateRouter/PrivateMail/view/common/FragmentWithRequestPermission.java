package com.PrivateRouter.PrivateMail.view.common;

import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;

public class FragmentWithRequestPermission extends Fragment {

    private static final int PERMISSIONS_REQUEST_CODE = 10000;
    private Runnable runnableAfterGetPermission;

    public boolean checkAndRequest(String permission, Runnable runnableAfterGetPermission) {
        if (ContextCompat.checkSelfPermission(getActivity(), permission)  != PackageManager.PERMISSION_GRANTED) {
            requestPermission(permission);
            this.runnableAfterGetPermission = runnableAfterGetPermission;
            return false;
        }
        else {
            return true;
        }

    }

    private void requestPermission(String permission) {
        ActivityCompat.requestPermissions(getActivity(),  new String[]{permission},  PERMISSIONS_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,  String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_CODE: {

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (runnableAfterGetPermission!=null)
                        runnableAfterGetPermission.run();
                }
            }

        }
    }
}
