package c.m.lapaksembakodonorojojepara.util.base


interface Presenter<T : View> {
    fun onAttach(view: T)

    fun onDetach()
}
