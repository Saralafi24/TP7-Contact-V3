package fr.tp.contacts

import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class CarteContactFragment: Fragment(){
 private val model:ContactsViewModel by activityViewModels()
 override fun onCreateView(inflater:LayoutInflater,container:ViewGroup?,state:Bundle?)=inflater.inflate(R.layout.fragment_map,container,false)
 override fun onViewCreated(view:View,state:Bundle?){ val msg=view.findViewById<TextView>(R.id.map_message); val c=model.get(requireArguments().getString("contact_id").orEmpty()); if(c==null||c.latitude==null||c.longitude==null||c.latitude !in -90.0..90.0||c.longitude !in -180.0..180.0){msg.text="Aucune coordonnée GPS valide pour ce contact.";msg.visibility=View.VISIBLE;return}; (childFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment)?.getMapAsync { map -> val p=LatLng(c.latitude,c.longitude);map.addMarker(MarkerOptions().position(p).title(c.displayName));map.moveCamera(CameraUpdateFactory.newLatLngZoom(p,14f)) } }
 companion object{fun newInstance(id:String)=CarteContactFragment().apply{arguments=Bundle().apply{putString("contact_id",id)}}}
}
