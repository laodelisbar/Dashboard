package CatatanTransaksi;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class DatabaseTransaksi1 extends SQLiteOpenHelper {

    private Context context;
    private static final String DATABASE_NAME = "BookLibraryTransaksi1.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "transaksi";
    private static final String COLUMN_ID = "id_transaksi";
    private static final String COLUMN_ID_KATEGORI = "id_kategori";
    private static final String COLUMN_SUMBER_DANA = "id_sumber_dana";
    private static final String COLUMN_TANGGAL = "tanggal";
    private static final String COLUMN_JUMLAH = "jumlah_uang";

    // Constructor DatabaseTransaksi
    public DatabaseTransaksi1(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Membuat tabel transaksi jika belum ada
        String query = "CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_ID_KATEGORI + " TEXT, " +
                COLUMN_SUMBER_DANA + " TEXT, " +
                COLUMN_TANGGAL + " TEXT, " +
                COLUMN_JUMLAH + " INTEGER, " +
                "FOREIGN KEY (" + COLUMN_ID_KATEGORI + ") REFERENCES kategori_pemasukkan(nama_kategori), " +
                "FOREIGN KEY (" + COLUMN_SUMBER_DANA + ") REFERENCES sumber_dana(nama_sumber_dana)" +
                ")";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop tabel transaksi jika sudah ada versi sebelumnya
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    // Method untuk menambahkan transaksi ke dalam database
    public boolean addTransaction(String namaKategori, String namaSumberDana, String tanggal, int jumlahUang) {
        SQLiteDatabase db = this.getWritableDatabase();

        if (db != null && db.isOpen()) {
            ContentValues cv = new ContentValues();

            // Menambahkan nilai ke dalam ContentValues
            cv.put(COLUMN_ID_KATEGORI, namaKategori);
            cv.put(COLUMN_SUMBER_DANA, namaSumberDana);
            cv.put(COLUMN_TANGGAL, tanggal);
            cv.put(COLUMN_JUMLAH, jumlahUang);

            // Memasukkan data ke dalam tabel transaksi
            long result = db.insert(TABLE_NAME, null, cv);
            db.close();

            // Mengembalikan true jika data berhasil dimasukkan, false jika gagal
            return result != -1;
        } else {
            // Koneksi database tidak terbuka
            return false;
        }
    }

    public ArrayList<String> getAllDates() {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<String> dates = new ArrayList<>();

        if (db != null && db.isOpen()) {
            String[] projection = {COLUMN_TANGGAL};
            Cursor cursor = db.query(TABLE_NAME, projection, null, null, null, null, null);

            if (cursor != null) {
                while (cursor.moveToNext()) {
                    String tanggal = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TANGGAL));
                    dates.add(tanggal);
                }
                cursor.close();
            }
        }
        return dates;
    }

    public ArrayList<String> getAllDays() {
        ArrayList<String> dates = getAllDates();
        ArrayList<String> days = new ArrayList<>();

        SimpleDateFormat sdfInput = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        SimpleDateFormat sdfOutput = new SimpleDateFormat("EEEE", Locale.getDefault());

        for (String tanggal : dates) {
            try {
                Date date = sdfInput.parse(tanggal);
                String hari = sdfOutput.format(date);
                days.add(hari);
            } catch (ParseException e) {
                e.printStackTrace();
                days.add("");
            }
        }
        return days;
    }
    public ArrayList<String> getAllNamaKategori() {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<String> namaKategoriList1 = new ArrayList<>();

        if (db != null && db.isOpen()) {
            String[] projection = {COLUMN_ID_KATEGORI};
            Cursor cursor = db.query(
                    TABLE_NAME,
                    projection,
                    null,
                    null,
                    null,
                    null,
                    null
            );

            if (cursor != null) {
                while (cursor.moveToNext()) {
                    String namaKategori = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ID_KATEGORI));
                    namaKategoriList1.add(namaKategori);
                }
                cursor.close();
            }
        }

        return namaKategoriList1;
    }

    public ArrayList<Integer> getAllJumlahUang() {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<Integer> jumlahUangList = new ArrayList<>();

        if (db != null && db.isOpen()) {
            String[] projection = {COLUMN_JUMLAH};
            Cursor cursor = db.query(
                    TABLE_NAME,
                    projection,
                    null,
                    null,
                    null,
                    null,
                    null
            );

            if (cursor != null) {
                while (cursor.moveToNext()) {
                    int jumlahUang = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_JUMLAH));
                    jumlahUangList.add(jumlahUang);
                }
                cursor.close();
            }
        }

        return jumlahUangList;
    }

    // Di kelas DatabaseTransaksi
    public ArrayList<Item> getAllItemsFromDatabase(String tanggal) {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<Item> itemList = new ArrayList<>();

        if (db != null && db.isOpen()) {
            String[] projection = {COLUMN_ID_KATEGORI, COLUMN_JUMLAH};
            String selection = COLUMN_TANGGAL + " = ?";
            String[] selectionArgs = {tanggal};

            Cursor cursor = db.query(
                    TABLE_NAME,
                    projection,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    null
            );

            if (cursor != null) {
                while (cursor.moveToNext()) {
                    String kategori = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ID_KATEGORI));
                    int jumlahUang = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_JUMLAH));
                    String harga = "Rp. " + jumlahUang;
                    itemList.add(new Item(kategori, harga));
                }
                cursor.close();
            }
        }

        return itemList;
    }



    public ArrayList<String> getAllNamaSumberDana() {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<String> namaSumberDanaList = new ArrayList<>();

        if (db != null && db.isOpen()) {
            String[] projection = {"nama_sumber_dana"};
            Cursor cursor = db.query(
                    "sumber_dana",
                    projection,
                    null,
                    null,
                    null,
                    null,
                    null
            );

            if (cursor != null) {
                while (cursor.moveToNext()) {
                    String namaSumberDana = cursor.getString(cursor.getColumnIndexOrThrow("nama_sumber_dana"));
                    namaSumberDanaList.add(namaSumberDana);
                }
                cursor.close();
            }
        }

        return namaSumberDanaList;
    }




}