package com.maiot.easybeach;

import android.location.Location;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface UmbrellaDAO {
    @Insert
    void insertAll(Umbrella[] u);

    @Insert
    void insertUmbrella(Umbrella u);

    @Query("SELECT * FROM Umbrella")
    List<Umbrella> getAll();

    @Query("UPDATE Umbrella SET type=:t,free=:f,position=:p WHERE uid=:id")
    void UpdateUmbrella(int id, char t, boolean f, Location p);

}
