package ong.myapp.cinematicketbooking.adapter;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ong.myapp.cinematicketbooking.R;

public class ShowDatesAdapter extends RecyclerView.Adapter<ShowDatesAdapter.ShowDatesViewHolder> {

    private List<String> showDates;
    private String movieId;
    private String theaterId;

    public ShowDatesAdapter(List<String> showDates, String movieId, String theaterId) {
        this.showDates = showDates;
        this.movieId = movieId;
        this.theaterId = theaterId;
    }

    @NonNull
    @Override
    public ShowDatesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_show_date, parent, false);
        return new ShowDatesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ShowDatesViewHolder holder, int position) {
        String date = showDates.get(position);
        holder.textViewDate.setText(date);

        // Xử lý sự kiện khi nhấn vào nút "Sửa"
        holder.btnEdit.setOnClickListener(view -> {
            Context context = view.getContext();

            // Tạo và hiển thị dialog để chỉnh sửa
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Chỉnh sửa suất chiếu");

            // Inflate layout dialog_edit_show
            View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_edit_show, null);
            builder.setView(dialogView);

            EditText dateStart = dialogView.findViewById(R.id.dateStart);
            RecyclerView recyclerViewStartTime = dialogView.findViewById(R.id.recyclerViewStartTime);
            Spinner spinnerGenreSub = dialogView.findViewById(R.id.spinnerGenreSub);
            Spinner spinnerGenreRap = dialogView.findViewById(R.id.spinnerGenreRap);
            ImageButton btnAddTime = dialogView.findViewById(R.id.btnAddTime);

            dateStart.setText(date);

            // Sử dụng DatePickerDialog khi người dùng nhấn vào EditText ngày
            dateStart.setOnClickListener(v -> {
                final Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(context,
                        (view1, selectedYear, selectedMonth, selectedDay) -> {
                            String formattedDate = String.format(Locale.getDefault(), "%02d/%02d/%d",
                                    selectedDay, selectedMonth + 1, selectedYear);
                            dateStart.setText(formattedDate);
                        }, year, month, day);
                datePickerDialog.show();
            });

            // Lấy thông tin từ Firestore
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("Show")
                    .whereEqualTo("movieId", movieId)
                    .whereEqualTo("theaterId", theaterId)
                    .whereEqualTo("date", date)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult() != null) {
                            for (DocumentSnapshot document : task.getResult().getDocuments()) {
                                List<String> startTimes = (List<String>) document.get("startTime");
                                if (startTimes == null) {
                                    startTimes = new ArrayList<>();
                                } else {
                                    startTimes = new ArrayList<>(startTimes);
                                }

                                // Sắp xếp startTimes
                                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
                                Collections.sort(startTimes, (time1, time2) -> {
                                    try {
                                        Date date1 = sdf.parse(time1);
                                        Date date2 = sdf.parse(time2);
                                        return date1.compareTo(date2);
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                        return 0;
                                    }
                                });

                                // Thiết lập Adapter cho RecyclerView để hiển thị các thời gian
                                StartTimeAdapter startTimeAdapter = new StartTimeAdapter(startTimes);
                                recyclerViewStartTime.setLayoutManager(new LinearLayoutManager(context));
                                recyclerViewStartTime.setAdapter(startTimeAdapter);

                                // Thêm thời gian mới
                                List<String> finalStartTimes = startTimes;
                                btnAddTime.setOnClickListener(v -> {
                                    finalStartTimes.add("");
                                    startTimeAdapter.notifyItemInserted(finalStartTimes.size() - 1);
                                });

                                // Thiết lập dữ liệu cho Spinner
                                String subType = document.getString("subType");
                                String theater = document.getString("theaterId");

                                // Thiết lập Spinner cho subType
                                List<String> subTypeOptions = new ArrayList<>();
                                subTypeOptions.add("2D PHỤ ĐỀ");
                                subTypeOptions.add("2D LỒNG TIẾNG");
                                subTypeOptions.add("3D PHỤ ĐỀ");
                                subTypeOptions.add("3D LỒNG TIẾNG");
                                ArrayAdapter<String> subTypeAdapter = new ArrayAdapter<>(context,
                                        android.R.layout.simple_spinner_item, subTypeOptions);
                                subTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                spinnerGenreSub.setAdapter(subTypeAdapter);
                                if (subType != null) {
                                    int spinnerPosition = subTypeAdapter.getPosition(subType);
                                    spinnerGenreSub.setSelection(spinnerPosition);
                                }

                                // Thiết lập Spinner cho theaterId
                                List<String> theaterOptions = new ArrayList<>();
                                theaterOptions.add("Rạp 1");
                                theaterOptions.add("Rạp 2");
                                theaterOptions.add("Rạp 3");
                                theaterOptions.add("Rạp 4");
                                theaterOptions.add("Rạp 5");
                                theaterOptions.add("Rạp 6");
                                theaterOptions.add("Rạp 7");
                                ArrayAdapter<String> theaterAdapter = new ArrayAdapter<>(context,
                                        android.R.layout.simple_spinner_item, theaterOptions);
                                theaterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                spinnerGenreRap.setAdapter(theaterAdapter);
                                if (theater != null) {
                                    int spinnerPosition = theaterAdapter.getPosition(theater);
                                    spinnerGenreRap.setSelection(spinnerPosition);
                                }
                            }
                        }
                    });

            // Lưu các thay đổi khi người dùng nhấn nút "Lưu"
            builder.setPositiveButton("Lưu", (dialogInterface, which) -> {
                String newDate = dateStart.getText().toString();

                // Lấy các giá trị từ Spinner
                String newSubType = spinnerGenreSub.getSelectedItem().toString();
                String newTheaterId = spinnerGenreRap.getSelectedItem().toString();

                // Lấy các thời gian từ adapter
                List<String> updatedTimes = new ArrayList<>();
                StartTimeAdapter startTimeAdapter = (StartTimeAdapter) recyclerViewStartTime.getAdapter();
                if (startTimeAdapter != null) {
                    updatedTimes.addAll(startTimeAdapter.getStartTimes());

                    // Sắp xếp lại danh sách trước khi cập nhật vào Firestore
                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
                    Collections.sort(updatedTimes, (time1, time2) -> {
                        try {
                            Date date1 = sdf.parse(time1);
                            Date date2 = sdf.parse(time2);
                            return date1.compareTo(date2);
                        } catch (ParseException e) {
                            e.printStackTrace();
                            return 0;
                        }
                    });
                }

                // Cập nhật lại Firestore với dữ liệu mới
                db.collection("Show")
                        .whereEqualTo("movieId", movieId)
                        .whereEqualTo("theaterId", theaterId)
                        .whereEqualTo("date", date)
                        .get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful() && task.getResult() != null) {
                                for (DocumentSnapshot document : task.getResult().getDocuments()) {
                                    db.collection("Show").document(document.getId())
                                            .update("date", newDate,
                                                    "startTime", updatedTimes,
                                                    "subType", newSubType,
                                                    "theaterId", newTheaterId)
                                            .addOnSuccessListener(aVoid ->
                                                    Toast.makeText(context, "Sửa thành công!", Toast.LENGTH_SHORT).show()
                                            )
                                            .addOnFailureListener(e ->
                                                    Toast.makeText(context, "Lỗi khi sửa!", Toast.LENGTH_SHORT).show()
                                            );
                                }
                            }
                        });
            });

            builder.setNegativeButton("Hủy", (dialogInterface, which) -> {
                // Hủy bỏ dialog, không cần làm gì thêm
            });

            AlertDialog dialog = builder.create();
            dialog.show();
        });



        // Xử lý sự kiện khi nhấn vào nút "Xóa"
        holder.btnDelete.setOnClickListener(view -> {
            // Kiểm tra điều kiện trước khi xóa
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            try {
                // Ngày hiện tại
                Calendar currentDate = Calendar.getInstance();

                // Ngày chiếu
                Calendar showDate = Calendar.getInstance();
                showDate.setTime(sdf.parse(date));

                // Tính khoảng cách ngày giữa ngày hiện tại và ngày chiếu
                long differenceInMillis = currentDate.getTimeInMillis() - showDate.getTimeInMillis();
                long daysDifference = differenceInMillis / (1000 * 60 * 60 * 24);

                if (daysDifference > 90) {
                    // Xóa mục này khỏi Firestore nếu ngày chiếu lớn hơn 90 ngày trước ngày hiện tại
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    db.collection("Show")
                            .whereEqualTo("movieId", movieId)
                            .whereEqualTo("theaterId", theaterId)
                            .whereEqualTo("date", date)
                            .get()
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful() && task.getResult() != null) {
                                    for (DocumentSnapshot document : task.getResult().getDocuments()) {
                                        db.collection("Show").document(document.getId()).delete()
                                                .addOnSuccessListener(aVoid -> {
                                                    // Xóa thành công, cập nhật lại giao diện
                                                    showDates.remove(position);
                                                    notifyItemRemoved(position);
                                                    notifyItemRangeChanged(position, showDates.size());
                                                    Toast.makeText(view.getContext(), "Xóa thành công!", Toast.LENGTH_SHORT).show();
                                                })
                                                .addOnFailureListener(e ->
                                                        Toast.makeText(view.getContext(), "Lỗi khi xóa!", Toast.LENGTH_SHORT).show()
                                                );
                                    }
                                }
                            });
                } else {
                    // Nếu ngày chiếu nhỏ hơn hoặc bằng 90 ngày so với ngày hiện tại, không được phép xóa
                    Toast.makeText(view.getContext(), "Không thể xóa vì ngày chiếu nhỏ hơn 90 ngày so với ngày hiện tại!", Toast.LENGTH_SHORT).show();
                }
            } catch (ParseException e) {
                e.printStackTrace();
                Toast.makeText(view.getContext(), "Lỗi khi phân tích ngày!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return showDates.size();
    }

    public static class ShowDatesViewHolder extends RecyclerView.ViewHolder {

        TextView textViewDate;
        ImageButton btnEdit, btnDelete;

        public ShowDatesViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewDate = itemView.findViewById(R.id.textViewDate);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}




