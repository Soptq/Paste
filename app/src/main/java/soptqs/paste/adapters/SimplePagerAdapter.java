package soptqs.paste.adapters;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import soptqs.paste.fragments.BaseFragment;

/**
 * Created by S0ptq on 2018/2/5.
 */

public class SimplePagerAdapter extends FragmentStatePagerAdapter implements ViewPager.OnPageChangeListener {

    private Context context;
    private BaseFragment[] fragments;

    public SimplePagerAdapter(Context context, ViewPager viewPager, FragmentManager fm, BaseFragment... fragments) {
        super(fm);
        this.context = context;
        this.fragments = fragments;
        viewPager.addOnPageChangeListener(this);
    }

    @Override
    public BaseFragment getItem(int position) {
        return fragments[position];
    }

    @Override
    public CharSequence getPageTitle(int position) {
//        Drawable image = ContextCompat.getDrawable(context, imageResId[position]);
//        if (image != null) {
//            image.setBounds(0, 0, image.getIntrinsicWidth(), image.getIntrinsicHeight());
//        }
//        SpannableString sb = new SpannableString(" ");
//        ImageSpan imageSpan = new ImageSpan(image, ImageSpan.ALIGN_BOTTOM);
//        sb.setSpan(imageSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//        return sb;
//        return fragments[position].getTitle(context);
        return null;
    }

    @Override
    public int getCount() {
        return fragments.length;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        fragments[position].onSelect();
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
