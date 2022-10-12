package pt.isec.amov.a2018020733.aula2

object MyObject {
    private var _my_value = 0

    init {
        _my_value = 1000
    }

    val my_value : Int
        get() = --_my_value
}