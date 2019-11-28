package c.m.marketplacedesa.util.base


interface Presenter<T : View> {
    fun onAttach(view: T)

    fun onDetach()
}
