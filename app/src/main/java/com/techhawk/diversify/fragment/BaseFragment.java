package com.techhawk.diversify.fragment;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.techhawk.diversify.R;

/**
 * Created by Yidi Wu on 25/3/18.
 */

public class BaseFragment extends Fragment {
    public ProgressDialog mProgressDialog;


    // Get current user ID from firebase
    public String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }


    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this.getContext());
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    public void loading() {
        new AsyncTask<Object, Void, Void>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                showProgressDialog();
            }

            @Override
            protected Void doInBackground(Object... objects) {
                try {
                    Thread.sleep(2000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                hideProgressDialog();
            }
        }.execute();
    }

    // Show a feedback
    public void feedback(String msg) {
        Toast toast = Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT);
        View v = toast.getView();
        v.setBackgroundResource(R.drawable.custom_toast);
        toast.setView(v);
        toast.show();
    }
}
