package main.emfk.com.emfklatest.Frags;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.util.Util;

import java.util.List;

import main.emfk.com.emfklatest.AlgorithmsActivity;
import main.emfk.com.emfklatest.DonateWebActivity;
import main.emfk.com.emfklatest.EMLocationMapActivity;
import main.emfk.com.emfklatest.EmedOnlineUpdates;
import main.emfk.com.emfklatest.R;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HomeFrag.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HomeFrag#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFrag extends Fragment implements IOnBackPressed {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public static String FACEBOOK_URL = "https://www.facebook.com/emergencymedkenya/";
    public static String FACEBOOK_PAGE_ID = "emergencymedkenya";
    public static String TELEGRAM_PAGE_ID = "EmergencyMedicineKenya";

    Context context;
    AppCompatButton btnDonate;
    AppCompatImageView btnInstagram, btnTelegram, btnLinkedIn,
            btnTwitter, btnWhatsapp, btnFaceBook;
    private PopupWindow mPopupWindow;
    private FrameLayout frameLayout;
    LinearLayout btnAmbulance, btnNearestMed, btnFirstAidHealth, btnAlgorithms, btnEmedOnlineUpdates;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public HomeFrag() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFrag.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFrag newInstance(String param1, String param2) {
        HomeFrag fragment = new HomeFrag();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        context = this.getActivity();

        //Layout buttons
        btnAmbulance = view.findViewById(R.id.btnAmbulance);
        btnNearestMed = view.findViewById(R.id.btnNearestMed);
        btnFirstAidHealth = view.findViewById(R.id.btnFirstAidHealth);
        btnAlgorithms = view.findViewById(R.id.btnAlgorithms);
        btnEmedOnlineUpdates = view.findViewById(R.id.btnEmedOnlineUpdates);

        //social media icons
        frameLayout = view.findViewById(R.id.callFrame);
        btnDonate = view.findViewById(R.id.btnDonate);

        btnInstagram = view.findViewById(R.id.btnInstagram);
        btnTelegram = view.findViewById(R.id.btnTelegram);
        btnLinkedIn = view.findViewById(R.id.btnLinkedIn);
        btnTwitter = view.findViewById(R.id.btnTwitter);
        btnWhatsapp = view.findViewById(R.id.btnWhatsapp);
        btnFaceBook = view.findViewById(R.id.btnFaceBook);
        btnDonate = view.findViewById(R.id.btnDonate);

        btnDonate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, DonateWebActivity.class));
            }
        });

        btnAmbulance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ambulanceCallMenu();
            }
        });

        btnNearestMed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mapsIntent = new Intent(context, EMLocationMapActivity.class);
                //Intent mapsIntent = new Intent(context, GenericWebActivity.class);
                //mapsIntent.putExtra("url",R.string.maps_url);
                startActivity(mapsIntent);
            }
        });

        btnFirstAidHealth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PostsFrag postsfrag = new PostsFrag();
                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                        android.R.anim.fade_out);
                fragmentTransaction.replace(R.id.frame, postsfrag, "posts");
                fragmentTransaction.commitAllowingStateLoss();
                ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Posts");

                //Toast.makeText(context, "Cicked the posts fragment!!", Toast.LENGTH_SHORT).show();
            }
        });

        btnAlgorithms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(context,"Cicked the linear layout!!",Toast.LENGTH_SHORT).show();
                Intent algos = new Intent(context, AlgorithmsActivity.class);
                startActivity(algos);

            }
        });

        btnEmedOnlineUpdates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent emedOnline = new Intent(context, EmedOnlineUpdates.class);
                startActivity(emedOnline);
            }
        });

        btnInstagram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openInstagramAccount();

            }
        });

        btnTelegram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchTelegram();

            }
        });

        btnLinkedIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLinkedIn();
            }
        });

        btnTwitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openTwtr("emergencymedke");

            }
        });

        btnWhatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchWhatsapp();

            }
        });

        btnFaceBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(context,"Cicked the linear layout!!",Toast.LENGTH_SHORT).show();
                launchFacebook();

            }
        });

        return view;
    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    //pop-up to show phone
    private void ambulanceCallMenu() {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
        // Inflate the custom layout/view
        View customView = inflater.inflate(R.layout.call_layout, null);

        // Initialize a new instance of popup window
        mPopupWindow = new PopupWindow(
                customView,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );

        if (mPopupWindow.isShowing()) {
            mPopupWindow.dismiss();
        }

        // Set an elevation value for popup window
        // Call requires API level 21

        if (Build.VERSION.SDK_INT >= 21) {
            mPopupWindow.setElevation(5.0f);
        }

        mPopupWindow.setOutsideTouchable(true);

        // Get a reference for the custom view close button
        //ImageButton closeButton = (ImageButton) customView.findViewById(R.id.ib_close);


        mPopupWindow.setAnimationStyle(R.style.Animation);
        mPopupWindow.showAtLocation(frameLayout, Gravity.CENTER, 0, 0);
    }

    //will keep it here incase the xml version doesn't work
    public void dialPhoneNumber(String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + phoneNumber));
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    @Override
    public boolean onBackPressed() {
        /*if (mPopupWindow.isShowing()) {
            //action not popBackStack
            mPopupWindow.dismiss();
            return true;
        } else {
           return false;
        }*/
        return true;

    }

    //socials
    public final void launchFacebook() {
        Intent facebookIntent = new Intent(Intent.ACTION_VIEW);
        String facebookUrl = getFacebookPageURL(context);
        facebookIntent.setData(Uri.parse(facebookUrl));
        startActivity(facebookIntent);
    }

    public void launchWhatsapp() {

        final String urlFb = "https://api.whatsapp.com/send?phone=+254716026396";
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(urlFb));

        final PackageManager packageManager = context.getPackageManager();
        List<ResolveInfo> list =
                packageManager.queryIntentActivities(intent,
                        PackageManager.MATCH_DEFAULT_ONLY);
        if (list.size() == 0) {
            final String urlBrowser = "https://api.whatsapp.com/send?phone=+254716026396";
            intent.setData(Uri.parse(urlBrowser));
        }

        startActivity(intent);
    }

    public void openWhatsApp() {
        String contact = "+254716026396"; // use country code with your phone number
        String url = "https://api.whatsapp.com/send?phone=" + contact;
        try {
            PackageManager pm =  context.getPackageManager();
            pm.getPackageInfo("com.whatsapp", PackageManager.GET_ACTIVITIES);
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
        } catch (PackageManager.NameNotFoundException e) {
            Toast.makeText(context, "Whatsapp app not installed in your phone", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    public void openTwtr(String twtrName) {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("twitter://user?screen_name=" + twtrName)));
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/#!/" + twtrName)));
        }
    }

    protected void openLinkedIn() {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("linkedin://emergencymedicinekenya"));
        final PackageManager packageManager = context.getPackageManager();
        final List<ResolveInfo> list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        if (list.isEmpty()) {
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://ke.linkedin.com/in/emergencymedicinekenya"));
        }
        startActivity(intent);
    }


    //method to get the right URL to use in the intent
    public String getFacebookPageURL(Context context) {
        PackageManager packageManager = context.getPackageManager();
        try {
            int versionCode = packageManager.getPackageInfo("com.facebook.katana", 0).versionCode;
            if (versionCode >= 3002850) { //newer versions of fb app
                return "fb://facewebmodal/f?href=" + FACEBOOK_URL;
            } else { //older versions of fb app
                return "fb://page/" + FACEBOOK_PAGE_ID;
            }
        } catch (PackageManager.NameNotFoundException e) {
            return FACEBOOK_URL; //normal web url
        }
    }

    public void launchTelegram() {

        Intent intent = null;
        try {
            try {
                context.getPackageManager().getPackageInfo("org.telegram.messenger", 0);//Check for Telegram Messenger App
            } catch (Exception e) {
                context.getPackageManager().getPackageInfo("org.thunderdog.challegram", 0);//Check for Telegram X App
            }
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("tg://resolve?domain=" + TELEGRAM_PAGE_ID));
        } catch (Exception e) { //App not found open in browser
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.telegram.me/" + TELEGRAM_PAGE_ID));
        }
        startActivity(intent);
    }

    public void openInstagramAccount() {
        Uri uri = Uri.parse("http://instagram.com/_u/emkfoundation");
        Intent insta = new Intent(Intent.ACTION_VIEW, uri);
        insta.setPackage("com.instagram.android");

        if (isIntentAvailable(context, insta)) {
            startActivity(insta);
        } else {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://instagram.com/emkfoundation")));
        }
    }

    private boolean isIntentAvailable(Context ctx, Intent intent) {
        final PackageManager packageManager = ctx.getPackageManager();
        List<ResolveInfo> list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }

}
