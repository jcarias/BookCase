package pt.iscte.daam.bookcase;

import android.os.AsyncTask;
import android.util.Log;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import pt.iscte.daam.bookcase.bo.BookCaseDbHelper;

/**
 * Created by DVF on 28-05-2016.
 */
public class BackupServiceTask extends AsyncTask<String, Void, Boolean> {
    @Override
    protected Boolean doInBackground(String... params)
    {
        try {

            String url = params[0];
            String user = params[1];
            String pass = params[2];
            String packageName = params[3];
            String uniqueUserId = params[4];
            String operationMode = params[5];

            JSch jsch = new JSch();
            Session session = jsch.getSession(user, url, 22);
            session.setTimeout(100000);
            session.setPassword(pass);

            // Avoid asking for key confirmation
            Properties prop = new Properties();
            prop.put("StrictHostKeyChecking", "no");
            session.setConfig(prop);

            session.connect();

            if(!session.isConnected())
                return false;

            Channel channel = session.openChannel("sftp");
            channel.connect();

            String dbName = "/data/data/" + packageName + "/databases/" + BookCaseDbHelper.DATABASE_NAME;

            if(operationMode == "BACKUP") {

                InputStream mOutput = new FileInputStream(dbName);

                ChannelSftp sftp = (ChannelSftp) channel;
                sftp.put(mOutput, this.getUniqueBackupName(uniqueUserId));

                mOutput.close();
                sftp.disconnect();

            } else if (operationMode == "RESTORE") {

                ChannelSftp sftp = (ChannelSftp) channel;
                InputStream restore = sftp.get(this.getUniqueBackupName(uniqueUserId));

                OutputStream mOutput = new FileOutputStream(dbName);
                byte[] mBuffer = new byte[1024];
                int mLength;
                while ((mLength = restore.read(mBuffer))>0)
                {
                    mOutput.write(mBuffer, 0, mLength);
                }
                mOutput.flush();
                mOutput.close();
                restore.close();

                sftp.disconnect();
            }

        } catch (Exception e) {
            Log.e("PROFILE", "Profile FTP Connection: " + e.getMessage());
        }

        return true;
    }

    private String getUniqueBackupName(String userId) {
        return "backupDB_" + userId + ".backup";
    }
}
