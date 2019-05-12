package hu.bme.aut.android.carmonitoringapp.database

object DbConstants {

    const val DATABASE_NAME = "data.db"
    const val DATABASE_VERSION = 2
    const val DATABASE_CREATE_ALL = Measure.DATABASE_CREATE
    const val DATABASE_DROP_ALL = Measure.DATABASE_DROP

    object Measure {
        const val DATABASE_TABLE = "measure"
        const val KEY_ROWID = "_id"
        const val KEY_LATITUDE = "latitude"
        const val KEY_LONGITUDE = "longitude"
        const val KEY_ACCX = "accX"
        const val KEY_ACCY = "accY"
        const val KEY_ACCZ = "accZ"
        const val KEY_TIME = "time"

        const val DATABASE_CREATE =
            """create table if not exists $DATABASE_TABLE (
			$KEY_ROWID integer primary key autoincrement,
			$KEY_LATITUDE  real not null,
			$KEY_LONGITUDE real not null,
			$KEY_ACCX real not null,
            $KEY_ACCY real not null,
            $KEY_ACCZ real not null,
            $KEY_TIME real not null
			); """

        const val DATABASE_DROP = "drop table if exists $DATABASE_TABLE;"
    }
}