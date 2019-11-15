package c.m.marketplacedesa.util

interface Presenter<T : View> {
    fun onAttach(view: T)
    fun onDetach()
}
