package ru.android.bluetooth.temp;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.widget.Toast;

import com.ipaulpro.afilechooser.utils.FileUtils;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.read.biff.BiffException;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import ru.android.bluetooth.R;
import ru.android.bluetooth.root.RootActivity;

/**
 * Created by yasina on 01.10.17.
 */

public class Temp extends RootActivity {

    @BindView(R.id.tabs)
    TabLayout tabLayout;
    @BindView(R.id.viewpager)
    ViewPager viewPager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.temp3);
        start();
    }


    @Override
    public void init() {
        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
        showFileChooser();

    }

    private static final int FILE_SELECT_CODE = 0;

    private void showFileChooser() {
        // Create the ACTION_GET_CONTENT Intent
        Intent getContentIntent = FileUtils.createGetContentIntent();

        Intent intent = Intent.createChooser(getContentIntent, "Select a file");
        startActivityForResult(intent, FILE_SELECT_CODE);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case FILE_SELECT_CODE:
                if (resultCode == RESULT_OK) {
                    // Get the Uri of the selected file
                    Uri uri = data.getData();
                    Log.d(TAG, "File Uri: " + uri.toString());
                    // Get the path

                    //String path = FileUtils.getPath(this, uri);

                    String path = FileUtils.getPath(this, uri);
                    try {
                        InputStream inputStream = getContentResolver().openInputStream(uri);

                        /*Workbook workbook = new XSSFWorkbook(inputStream);
                        Sheet firstSheet = workbook.getSheetAt(0);
                        Iterator<Row> iterator = firstSheet.iterator();

                        while (iterator.hasNext()) {
                            Row nextRow = iterator.next();
                            Iterator<Cell> cellIterator = nextRow.cellIterator();

                            while (cellIterator.hasNext()) {
                                Cell cell = cellIterator.next();

                                switch (cell.getCellType()) {
                                    case Cell.CELL_TYPE_STRING:
                                        Log.d(TAG, cell.getStringCellValue());
                                        break;
                                    case Cell.CELL_TYPE_BOOLEAN:
                                        Log.d(TAG, cell.getBooleanCellValue() + "");
                                        break;
                                    case Cell.CELL_TYPE_NUMERIC:
                                        Log.d(TAG, cell.getNumericCellValue() + "");
                                        break;
                                }
                                System.out.print(" - ");
                            }
                            System.out.println();
                        }*/
                        WorkbookSettings wbSettings = new WorkbookSettings();
                        wbSettings.setLocale(new Locale("en", "EN"));
                        Workbook workbook;


                            try {
                                workbook = Workbook.getWorkbook(inputStream);
                                Sheet sheet = workbook.getSheet(0);
                                Log.d("f","f");
//                                WritableSheet sheet = (WritableSheet) workbook.getSheet(0);
                                for(int i=0; i<sheet.getColumns(); i++){
                                    Cell[] cells = sheet.getRow(i);
                                    for(int j=0; j< cells.length; j++){
                                        Cell c = cells[j];
                                        Log.d(TAG, c.getContents());
                                    }
                                }


                            } catch (BiffException e) {
                                e.printStackTrace();
                            }




                        inputStream.close();
                        //getStringFromInputStream(inputStream);
                        /*if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                            String result = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
                            Log.d(TAG, result);
                        }*/
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    // Alternatively, use FileUtils.getFile(Context, Uri)
                    if (path != null && FileUtils.isLocal(path)) {
                        File file = new File(path);
                    }
                    Log.d(TAG, "File Path: " + path);
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    private static String getStringFromInputStream(InputStream is) {

        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();

        String line;
        try {

            br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            while ((line = br.readLine()) != null) {
                //sb.append(line);

                Log.d("TAG", line);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return sb.toString();

    }


    @Override
    public void setClickListeners() {

    }

    @Override
    public void setTag() {

    }

    private void setupViewPager(final ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new OneFragment(), "Январь");
        adapter.addFragment(new OneFragment(), "Февраль");
        adapter.addFragment(new OneFragment(), "Март");

        adapter.addFragment(new OneFragment(), "Апрель");
        adapter.addFragment(new OneFragment(), "Май");
        adapter.addFragment(new OneFragment(), "Июнь");

        adapter.addFragment(new OneFragment(), "Июль");
        adapter.addFragment(new OneFragment(), "Август");
        adapter.addFragment(new OneFragment(), "Сентябрь");

        adapter.addFragment(new OneFragment(), "Октябрь");
        adapter.addFragment(new OneFragment(), "Ноябрь");
        adapter.addFragment(new OneFragment(), "Декабрь");

        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

}
