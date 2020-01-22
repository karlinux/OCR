package com.google.android.gms.samples.vision.ocrreader;
//2836075738
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import static android.provider.BaseColumns._ID;

public class Handler_sqlite extends SQLiteOpenHelper {

	private final String INE = "INE"; //TABLA

	private final String NOMBRE = "NOMBRE";
	private final String PATERNO = "PATERNO";
	private final String MATERNO = "MATERNO";

	private final String DOMICILIO = "DOMICILIO";
	private final String MUNICIPIO = "MUNICIPIO";
	private final String LOCALIDAD = "LOCALIDAD";
	private final String SECCION = "SECCION";
	private final String ESTADO = "ESTADO";
	private final String IMEI = "IMEI";
	private final String GUARDADO = "guardado";
	private final String FECHA = "fecha";
	private final String ESTATUS = "estatus";


	private final String SESION = "SESION";

	private final String PASSWORD = "PASSWORD";
	private final String USUARIO = "USUARIO";



	public Handler_sqlite(Context ctx) {

		super(ctx, "mibase", null, 1);
	}

	@Override
	public void onCreate(SQLiteDatabase db)
	{

		String query = "CREATE TABLE " + INE + "("+
				_ID+" INTEGER PRIMARY KEY AUTOINCREMENT, " +
				NOMBRE + " text, " +
				PATERNO + " text, " +
				MATERNO +" text, " +
				DOMICILIO +" text, " +
				ESTADO + " text," +
				MUNICIPIO + " text," +
				LOCALIDAD + " text," +
				SECCION + " text," +
				IMEI + " text, " +
				FECHA + " TIMESTAMP default (datetime('now', 'localtime')), " +
				GUARDADO + " INTEGER DEFAULT 1, " +
				ESTATUS + " INTEGER DEFAULT 0)";
		db.execSQL(query);

		String query2 = "CREATE TABLE " + SESION + "("+
				_ID+" INTEGER PRIMARY KEY AUTOINCREMENT, " +
				USUARIO + " text," +
				PASSWORD + " text," +
				IMEI + " text, " +
				FECHA + " TIMESTAMP default (datetime('now', 'localtime')), " +
				GUARDADO + " INTEGER DEFAULT 1, " +
				ESTATUS + " INTEGER DEFAULT 0)";
		db.execSQL(query2);

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int version_ant, int version_nue)
	{
		//db.execSQL("DROP TABLE IF EXISTS USUARIOS");
		//onCreate(db);

	}

	public void insertarRegistro(String nombre, String paterno, String materno, String domicilio, String estado, String municipio, String localidad)
	{
		ContentValues valores = new ContentValues();
		valores.put(NOMBRE, nombre);
		valores.put(PATERNO, paterno);
		valores.put(MATERNO, materno);
		valores.put(DOMICILIO, domicilio);
		valores.put(ESTADO, estado);
		valores.put(MUNICIPIO, municipio);
		valores.put(LOCALIDAD, localidad);

		if ( (nombre != null) && (!nombre.equals("")) ) {
			this.getWritableDatabase().insert(INE , null, valores);
		}
	}

	///////////////////////////////    Registro  2da parte   ////////////////////////////////////////

	public void actualzaRegistro1(String id, String correo, String password)
	{
		ContentValues valores = new ContentValues();
		valores.put(USUARIO, correo);
		valores.put(PASSWORD, password);

		this.getWritableDatabase().update(INE, valores, _ID + "=?", new String[] {id});
	}

	/////////////////////////////////    _ID Registro     ////////////////////////////////////////
	public int id(){
		int result=0;
		String columnas[] = {_ID};
		try{
			Cursor c = this.getReadableDatabase().query(INE, columnas,  null, null, null, null, null);
			c.moveToLast();
			if ( c.getCount() > 0) {
				result = c.getInt(0);
			}
		}catch(SQLiteException e){
			System.err.println("Exception @ rawQuery: " + e.getMessage());
			{
				result=0;
			}

		}
		return result;
	}

	public String getGuardado(){
		String result="0";
		String columnas[] = {GUARDADO};
		try{
			Cursor c = this.getReadableDatabase().query(INE, columnas,  null, null, null, null, null);
			c.moveToLast();
			if ( c.getCount() > 0) {
				int iu;
				iu = c.getColumnIndex(GUARDADO);
				result = c.getString(iu);
			}
		}catch(SQLiteException e){
			System.err.println("Exception @ rawQuery: " + e.getMessage());
			{
				result="0";
			}

		}
		return result;
	}

	public String getCampo(String campo ){
		String result="0";
		String columnas[] = {campo};
		try{
			Cursor c = this.getReadableDatabase().query(INE, columnas,  null, null, null, null, null);
			c.moveToLast();
			if ( c.getCount() > 0) {
				int iu;
				iu = c.getColumnIndex(campo);
				result = c.getString(iu);
			}
		}catch(SQLiteException e){
			System.err.println("Exception @ rawQuery: " + e.getMessage());
			{
				result="0";
			}

		}
		return result;
	}
	public Cursor getDatos(){
		String columnas[] = {NOMBRE, PATERNO, MATERNO, DOMICILIO, ESTADO, MUNICIPIO, LOCALIDAD};
		String[] ident= {"1"};
		Cursor c = this.getReadableDatabase().query(INE, columnas, null, null, null, null, null);
		return c;
	}
	public void abrir() {
		// abrir base
		this.getWritableDatabase();
	}

	public void cerrar() {
		this.close();
	}
}
