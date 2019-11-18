package c.m.marketplacedesa.util

import android.os.Handler
import android.util.Log
import androidx.annotation.Nullable
import androidx.lifecycle.LiveData
import com.google.firebase.firestore.*

class FirebaseQueryLiveData(private val query: Query) : LiveData<QuerySnapshot>() {
    private val valueEventListener = ValueEventListener()
    private var listenerRegistration: ListenerRegistration? = null

    private var listenerRemovePending = false
    private val handler = Handler()

    private val removeListener = Runnable {
        listenerRegistration!!.remove()
        listenerRemovePending = false
    }

    override fun onActive() {
        super.onActive()

        Log.d("QUERYSTATE", "ONACTIVE")

        if (listenerRemovePending) {
            handler.removeCallbacks(removeListener)
        } else {
            listenerRegistration = query.addSnapshotListener(valueEventListener)
        }
        listenerRemovePending = false
    }

    override fun onInactive() {
        super.onInactive()

        Log.d("QUERYSTATE", "ONINACTIVE")

        // Listener removal is schedule on a two second delay
        handler.postDelayed(removeListener, 2000)
        listenerRemovePending = true
    }

    private inner class ValueEventListener : EventListener<QuerySnapshot> {
        override fun onEvent(@Nullable querySnapshot: QuerySnapshot?, @Nullable e: FirebaseFirestoreException?) {
            if (e != null) {
                Log.e("Error", "Can't listen to query snapshots: %s", e)
                return
            }
            value = querySnapshot
        }
    }
}