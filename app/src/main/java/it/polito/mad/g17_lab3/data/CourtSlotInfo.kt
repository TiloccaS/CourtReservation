package it.polito.mad.g17_lab3.data

import com.google.firebase.firestore.DocumentReference

class CourtSlotInfo {
    private var id:DocumentReference? = null
    private var slot_id:DocumentReference? =null
    private var court_name = ""
    private var court_address = ""
    private var sport = ""
    private var opening = ""
    private var closing = ""
    private var selected = false
    private var people: Int = 1
    private var reservation_id: DocumentReference? = null
    private var equipment = 0
    private var date = ""
    private var authorId = ""
    fun getId(): DocumentReference? {
        return id
    }

    fun getSlotId(): DocumentReference? {
        return slot_id
    }
    fun setSlotId(id:DocumentReference) {
        slot_id=id
    }

    fun getCourtName(): String {
        return court_name
    }

    fun getDate(): String {
        return date
    }

    fun setDate(dt: String) {
        date = dt
    }

    fun getCourtSport(): String {
        return sport
    }
    fun setCourtSport(sp:String) {
        sport=sp
    }

    fun getCourtAddress(): String {
        return court_address
    }

    fun getCourtOpening(): String {
        return opening
    }

    fun getCourtClosing(): String {
        return closing
    }
    fun setCourtOpening(op:String) {
        opening=op
    }
    fun setCourtClosing(cl:String) {
        closing=cl
    }

    fun setCourtName(courtName: String) {
        court_name = courtName
    }

    fun setByCourtSlot(courtSlot: CourtSlot) {
        this.id = courtSlot.court_id
        this.slot_id = courtSlot.id
        this.sport = courtSlot.sport
        this.opening = courtSlot.time_start
        this.closing = courtSlot.time_finish
    }

    fun isSelected(): Boolean {
        return selected
    }

    fun setSelected(selected: Boolean) {
        this.selected = selected
    }

    fun setPeople(new_people: Int) {
        this.people = new_people
    }

    fun getPeople(): Int {
        return people
    }

    fun setEquipment(new_equipment: Int) {
        this.equipment = new_equipment
    }

    fun getEquipment(): Int {
        return equipment
    }

    fun setReservationId(reservation_id: DocumentReference) {
        this.reservation_id = reservation_id
    }
    fun getAuthorId(): String {
        return authorId
    }

    fun setReservationId(authorId: String) {
        this.authorId = authorId
    }


}