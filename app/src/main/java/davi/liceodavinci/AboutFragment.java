package davi.liceodavinci;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.vansuita.materialabout.builder.AboutBuilder;
import com.vansuita.materialabout.views.AboutView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

@SuppressLint("ValidFragment")
public class AboutFragment extends Fragment{

    Activity activity;

    @SuppressLint("ValidFragment")
    public AboutFragment(Activity activity){
        this.activity = activity;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.developers_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bitmap profile_pic1;
        Bitmap profile_pic2;

        LinearLayout fl = activity.findViewById(R.id.developer_frame);

        File image1 = new File(activity.getFilesDir(), "cason-profile");
        try {
            profile_pic1 = BitmapFactory.decodeStream(new FileInputStream(image1));
        } catch (FileNotFoundException e) {
            profile_pic1 = BitmapFactory.decodeResource(getResources(), R.mipmap.profile_picture);
        }

        File image2 = new File(activity.getFilesDir(), "baldin-profile");
        try {
            profile_pic2 = BitmapFactory.decodeStream(new FileInputStream(image2));
        } catch (FileNotFoundException e) {
            profile_pic2 = BitmapFactory.decodeResource(getResources(), R.mipmap.profile_picture);
        }

        if (profile_pic1 == null){
            profile_pic1 = BitmapFactory.decodeResource(getResources(), R.mipmap.profile_picture);
        }

        if (profile_pic2 == null){
            profile_pic2 = BitmapFactory.decodeResource(getResources(), R.mipmap.profile_picture);
        }

        AboutView aboutView1 = AboutBuilder.with(activity)
                .setPhoto(profile_pic1)
                .setCover(R.mipmap.profile_cover)
                .setName("Emanuele Cason")
                .setSubTitle("Sviluppatore client side")
                .setBrief("I'm warmed of mobile technologies. Ideas maker, curious and nature lover.")
                .setAppIcon(R.mipmap.ic_launcher)
                .setAppName(R.string.app_name)
                .setLinksColumnsCount(4)
                .addEmailLink("emanuele.cason@gmail.com")
                .addTwitterLink("emanuelecason")
                .addGitHubLink("emanuele-cason")
                .addBitbucketLink("emanuele_cason")
                .addFiveStarsAction()
                .setVersionNameAsAppSubTitle()
                .addShareAction(R.string.app_name)
                .setWrapScrollView(true)
                .setLinksAnimated(true)
                .setShowAsCard(true)
                .build();

        AboutView aboutView2 = AboutBuilder.with(activity)
                .setPhoto(profile_pic2)
                .setCover(R.mipmap.profile_cover)
                .setName("Leonardo Baldin")
                .setSubTitle("Sviluppatore server side")
                .setBrief("I'm warmed of mobile technologies. Ideas maker, curious and nature lover.")
                .setAppIcon(R.mipmap.ic_ziqqurat_api)
                .setAppName("webapi-dav")
                .setLinksColumnsCount(4)
                .addEmailLink("leobaldin.2000@gmail.com")
                .addTwitterLink("Baldin_L")
                .addGitHubLink("Baldomo")
                .addBitbucketLink("Baldomo")
                .setWrapScrollView(true)
                .setLinksAnimated(true)
                .setShowAsCard(true)
                .build();

        fl.addView(aboutView1);
        fl.addView(aboutView2);

    }
}
