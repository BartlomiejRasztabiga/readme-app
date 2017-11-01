package pl.infinitefuture.readme.about;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.common.base.Throwables;

import java.util.Calendar;

import mehdi.sakout.aboutpage.AboutPage;
import mehdi.sakout.aboutpage.Element;
import pl.infinitefuture.readme.R;

public class AboutFragment extends Fragment {

    public AboutFragment() {

    }

    public static AboutFragment newInstance() {
        return new AboutFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return new AboutPage(getActivity())
                .isRTL(false)
                .setImage(R.drawable.ic_book_black_24dp)
                .addItem(new Element().setTitle(getString(R.string.version) + " " + versionName()))
                .addGroup(getString(R.string.connect_with_us))
                .addEmail("kontakt@bartlomiej-rasztabiga.pl")
                .addWebsite("http://google.com")
                .addFacebook("ReadMeApp")
                .addPlayStore("pl.infinitefuture.readme")
                .addGroup("Credits")
                .addItem(getCreditsElement())
                .addItem(getCopyRightsElement())
                .create();
    }

    private Element getCopyRightsElement() {
        Element copyRightsElement = new Element();
        final String copyrights = String.format(getString(R.string.copy_right), Calendar.getInstance().get(Calendar.YEAR));
        copyRightsElement.setTitle(copyrights);
        copyRightsElement.setGravity(Gravity.CENTER);
        copyRightsElement.setOnClickListener(v ->
                Toast.makeText(getContext(), copyrights, Toast.LENGTH_SHORT).show());
        return copyRightsElement;
    }

    private Element getCreditsElement() {
        Element creditsElement = new Element();
        String credits = "Icons made by Freepik from www.flaticon.com";
        creditsElement.setTitle(credits);
        return creditsElement;
    }

    private String versionName() {
        String versionName = "";
        try {
            PackageInfo packageInfo = getActivity().getPackageManager().getPackageInfo(
                    getActivity().getPackageName(), 0);
            versionName = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("AboutFragment", Throwables.getStackTraceAsString(e));
        }
        return versionName;
    }
}
