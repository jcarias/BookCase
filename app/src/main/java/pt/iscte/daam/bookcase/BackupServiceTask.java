package pt.iscte.daam.bookcase;

import android.os.AsyncTask;
import android.util.Log;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import java.io.FileInputStream;
import java.io.InputStream;
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

            JSch jsch = new JSch();
            Session session = jsch.getSession(user, url, 22);
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

            String outFileName = "/data/data/" + packageName + "/databases/" + BookCaseDbHelper.DATABASE_NAME;
            InputStream mOutput = new FileInputStream(outFileName);

            ChannelSftp sftp = (ChannelSftp) channel;
            sftp.put(mOutput, this.getUniqueBackupName(uniqueUserId));

            mOutput.close();

        } catch (Exception e) {
            Log.e("PROFILE", "Profile FTP Connection: " + e.getMessage());
        }

        return true;
    }

    private String getUniqueBackupName(String userId) {
        return "backupDB_" + userId + ".backup";
    }
}
