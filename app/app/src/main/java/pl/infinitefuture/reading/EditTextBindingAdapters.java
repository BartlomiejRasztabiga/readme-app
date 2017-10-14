package pl.infinitefuture.reading;

import android.databinding.BindingConversion;
import android.databinding.InverseBindingAdapter;
import android.util.Log;
import android.widget.EditText;

import com.google.common.base.Throwables;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import pl.infinitefuture.reading.addeditbook.InvalidDateException;

public class EditTextBindingAdapters {

    private static final int BOOK_DATE_LENGTH = 10;

    private EditTextBindingAdapters() {}

    @BindingConversion
    public static String longToStr(Long value) {
        return value != null ? String.valueOf(value) : "";
    }

    @InverseBindingAdapter(attribute = "android:text", event = "android:textAttrChanged")
    public static Long captureLongValue(EditText view) {
        long value = 0;
        try {
            value = Long.parseLong(view.getText().toString());
        } catch (NumberFormatException e) {
            Log.d("EditTextBindingAdapters", Throwables.getStackTraceAsString(e));
        }
        return value;
    }

    @BindingConversion
    public static String dateToStr(Date value) {
        DateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        return value != null ? sdf.format(value) : "date";
    }

    public static Date strToDate(String value) throws InvalidDateException, ParseException {
        if (value == null || !isValidDate(value)) {
            throw new InvalidDateException("Invalid date: " + value);
        }
        DateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        return sdf.parse(value);
    }

    public static boolean isValidDate(String date) {
        if (date.length() != BOOK_DATE_LENGTH) return false;

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        Date testDate;
        try {
            testDate = sdf.parse(date);
        }
        catch (ParseException e) {
            return false;
        }
        return sdf.format(testDate).equals(date);

    }

/*    @InverseBindingAdapter(attribute = "android:text", event = "android:textAttrChanged")
    public static Date captureDateValue(EditText view) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(view.getText().toString().substring(0, 2)));
        calendar.set(Calendar.MONTH, Integer.parseInt(view.getText().toString().substring(2, 4)));
        calendar.set(Calendar.YEAR, Integer.parseInt(view.getText().toString().substring(4, 8)));

        return calendar.getTime();
    }*/
}