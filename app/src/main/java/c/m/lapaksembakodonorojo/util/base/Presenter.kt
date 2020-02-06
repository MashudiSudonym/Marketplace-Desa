package c.m.lapaksembakodonorojo.util.base


interface Presenter<T : View> {
    fun onAttach(view: T)

    fun onDetach()
}
