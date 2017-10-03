package pl.infinitefuture.reading;

import android.databinding.BindingConversion;
import android.databinding.InverseBindingAdapter;
import android.util.Log;
import android.widget.EditText;

import com.google.common.base.Throwables;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class EditTextBindingAdapters {

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

    //TODO refactor
    public static Date strToDate(String value) {
        DateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        try {
            return sdf.parse(value);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return new Date();
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