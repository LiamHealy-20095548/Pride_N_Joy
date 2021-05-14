package ie.wit.pridenjoy.models

interface EventPlacemarkStore {
    //fun findAll(): List<EventModel>
    //fun create(placemark: EventModel)
    //fun update(placemark: EventModel)
    //fun delete(placemark: EventModel)

    fun findById(id:Long) : EventModel?
}
