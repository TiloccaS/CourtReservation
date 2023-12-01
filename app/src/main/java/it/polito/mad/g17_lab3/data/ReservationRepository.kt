package it.polito.mad.g17_lab3.data

import android.app.Application
import androidx.lifecycle.LiveData

class ReservationRepository(application: Application) {
    /*
    private val courtDao = ReservationDatabase.getDatabase(application).courtDao()
    private val courtSlotDao = ReservationDatabase.getDatabase(application).courtSlotDao()
    private val ReservationDao = ReservationDatabase.getDatabase(application).reservationDao()
    private val RatingDao = ReservationDatabase.getDatabase(application).ratingDao()

    fun update(CourtSloat: CourtSlot) {
        courtSlotDao.update(CourtSloat)
    }

    fun freeSlot(courtId: String, time: String, date: String) {
        courtSlotDao.freeSlot(courtId, time, date)
    }

    fun delete(r: Reservation) {
        return ReservationDao.delete(r)
    }

    fun freeSlotWithId(id: String) {
        return courtSlotDao.freeSlotWithId(id)
    }

    fun getAll(): LiveData<List<Court>> {
        val tmp = courtDao.getAll()
        return courtDao.getAll()
    }

    fun addCourt(court: Court) {

        courtDao.save(court)

    }

    fun addRating(courtRating: RatingCourt) {
        RatingDao.save(courtRating)
    }

    fun getAllRating(): LiveData<List<RatingCourt>> {
        return RatingDao.getAll()
    }

    fun addCourtSlot(court: CourtSlot) {

        courtSlotDao.save(court)
    }

    fun getFreeSlot(): LiveData<List<CourtSlot>> {
        return courtSlotDao.getFreeSlot()

    }

    fun addReservation(reservation: Reservation) {
         ReservationDao.save(reservation)

    }

    fun getReservation(): LiveData<List<Reservation>> {
        return ReservationDao.getAll()
    }


    fun update(reservation: Reservation) {
        ReservationDao.update(reservation)
    }

    fun clear() {
        courtDao.clear()
        ReservationDao.clear()
        courtSlotDao.clear()
        RatingDao.clear()
    }

     */


}
