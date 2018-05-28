package com.ccr.aricheditor.selectphoto;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


import com.ccr.aricheditor.R;

import java.util.List;

/**
 * 图片点击大图预览页面
 * <p/>
 * Created by donglua on 15/6/24.
 */
public class PhotoPagerActivity extends AppCompatActivity {

    private ImagePagerFragment pagerFragment;

    public final static String EXTRA_CURRENT_ITEM = "current_item";
    public final static String EXTRA_PHOTOS = "photos";
    public final static String EXTRA_SHOW_DELETE = "show_delete";

    private ActionBar actionBar;
    private boolean showDelete;
    private List<String> paths;
    private int endItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_photo_pager);

        //获取当前轮播图中图片的位置
        final int currentItem = getIntent().getIntExtra(EXTRA_CURRENT_ITEM, 0);

        paths = getIntent().getStringArrayListExtra(EXTRA_PHOTOS);
        showDelete = getIntent().getBooleanExtra(EXTRA_SHOW_DELETE, true);

        pagerFragment =
                (ImagePagerFragment) getSupportFragmentManager().findFragmentById(R.id.photoPagerFragment);
        //初始化頁面顯示
        pagerFragment.setPhotos(paths, currentItem);

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        actionBar = getSupportActionBar();

        actionBar.setDisplayHomeAsUpEnabled(true);
        updateActionBarTitle();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            actionBar.setElevation(25);
        }

        /**
         * 页面改变事件
         */
        pagerFragment.getViewPager().addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                updateActionBarTitle();
            }

            @Override
            public void onPageSelected(int i) {

            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (showDelete) {
            getMenuInflater().inflate(R.menu.menu_preview, menu);
        }
        return true;
    }


    @Override
    public void onBackPressed() {

        Intent intent = new Intent();
        intent.putExtra(PhotoPickerActivity.KEY_SELECTED_PHOTOS, pagerFragment.getPaths());
        intent.putExtra("CurrentItem", pagerFragment.getCurrentItem());
        setResult(RESULT_OK, intent);
        finish();
        super.onBackPressed();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }

        if (item.getItemId() == R.id.delete) {
            final int index = pagerFragment.getCurrentItem();

            final String deletedPath = pagerFragment.getPaths().get(index);

            Snackbar snackbar = Snackbar.make(pagerFragment.getView(), R.string.picker_deleted_a_photo,
                    Snackbar.LENGTH_LONG);

            if (pagerFragment.getPaths().size() <= 1) {

                // show confirm dialog
                new AlertDialog.Builder(this)
                        .setTitle(R.string.picker_confirm_to_delete)
                        .setPositiveButton(R.string.picker_yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                                pagerFragment.getPaths().clear();
                                Intent intent = new Intent();
                                intent.putExtra(PhotoPickerActivity.KEY_SELECTED_PHOTOS, pagerFragment.getPaths());
                                setResult(RESULT_OK, intent);
                                finish();
                            }
                        })
                        .setNegativeButton(R.string.picker_cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        })
                        .show();

            } else {
                snackbar.show();

                pagerFragment.getPaths().remove(index);
                //pagerFragment.getViewPager().removeViewAt(index);
                pagerFragment.getViewPager().getAdapter().notifyDataSetChanged();
            }

            snackbar.setAction(R.string.picker_undo, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (pagerFragment.getPaths().size() > 0) {
                        pagerFragment.getPaths().add(index, deletedPath);
                    } else {
                        pagerFragment.getPaths().add(deletedPath);
                    }
                    pagerFragment.getViewPager().getAdapter().notifyDataSetChanged();
                    pagerFragment.getViewPager().setCurrentItem(index, true);
                }
            });

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void updateActionBarTitle() {
        actionBar.setTitle(
                getString(R.string.picker_image_index, pagerFragment.getViewPager().getCurrentItem() + 1,
                        pagerFragment.getPaths().size()));
    }
}
