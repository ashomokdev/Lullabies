package com.ashomok.lullabies;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;

import com.ashomok.lullabies.services.playback.MediaNotificationManager;
import com.ashomok.lullabies.services.playback.MusicService;
import com.ashomok.lullabies.tools.LogHelper;

/**
 * Created by iuliia on 7/10/16.
 */
public class ExitDialogFragment extends DialogFragment {

    private static final String TAG = LogHelper.makeLogTag(ExitDialogFragment.class);

    public static ExitDialogFragment newInstance(int title) {
        ExitDialogFragment frag = new ExitDialogFragment();
        Bundle args = new Bundle();
        args.putInt("title", title);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int title = getArguments().getInt("title");

        return new AlertDialog.Builder(getActivity())
                .setTitle(title)
                .setPositiveButton(R.string.alert_dialog_exit,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {

                                //stop music
                                getActivity().finishAffinity();

                            }
                        }
                )
                .setNegativeButton(R.string.alert_dialog_cancel,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                //nothing
                            }
                        }
                )
                .create();
    }
}
