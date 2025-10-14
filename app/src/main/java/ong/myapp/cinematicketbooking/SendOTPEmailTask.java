package ong.myapp.cinematicketbooking;



import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

public class SendOTPEmailTask extends AsyncTask<String, Void, Boolean> {
    private final Context context;
    private final String senderPassword;

    public SendOTPEmailTask(Context context, String senderPassword) {
        this.context = context;
        this.senderPassword = senderPassword;
    }

    @Override
    protected Boolean doInBackground(String... params) {
        String email = params[0];
        String otp = params[1];
        MailSender mailSender = new MailSender();

        String subject = "[Xác nhận OTP] CINENOVA CINEMA";
        String body = "<h3>Xin chào!</h3>" +
                "<p>Đây là mã OTP của bạn: <b>" + otp + "</b></p>" +
                "<p>Vui lòng không chia sẻ mã OTP này với ai khác.</p>" +
                "<p>Trân trọng,</p>" +
                "<p>Cinenova Cinema</p>";

        try {
            mailSender.sendEmail(email, subject, body, senderPassword);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean result) {
        if (result) {
            Toast.makeText(context, "Đã gửi mã OTP qua email!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Gửi email thất bại!", Toast.LENGTH_SHORT).show();
        }
    }
}
