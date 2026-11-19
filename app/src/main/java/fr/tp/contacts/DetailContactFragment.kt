package fr.tp.contacts

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import java.io.File

class DetailContactFragment : Fragment() {
    private val model: ContactsViewModel by activityViewModels(); private var contact: Contact?=null; private var photoUri:String?=null; private var cameraUri:Uri?=null
    private val gallery=registerForActivityResult(ActivityResultContracts.GetContent()){ uri -> if(uri!=null){photoUri=uri.toString(); view?.findViewById<ImageView>(R.id.photo)?.setImageURI(uri)} }
    private val camera=registerForActivityResult(ActivityResultContracts.TakePicture()){ ok -> if(ok) {photoUri=cameraUri.toString(); view?.findViewById<ImageView>(R.id.photo)?.setImageURI(cameraUri)} }
    override fun onCreateView(inflater:LayoutInflater,container:ViewGroup?,state:Bundle?)=inflater.inflate(R.layout.fragment_detail,container,false)
    override fun onViewCreated(v:View,state:Bundle?) {
        val id=requireArguments().getString(ARG_ID).orEmpty(); contact=model.get(id); val c=contact
        if(c==null){Toast.makeText(requireContext(),"Contact introuvable",Toast.LENGTH_SHORT).show(); parentFragmentManager.popBackStack(); return}
        val first=v.findViewById<EditText>(R.id.first); val last=v.findViewById<EditText>(R.id.last); val phone=v.findViewById<EditText>(R.id.phone); val email=v.findViewById<EditText>(R.id.email); val lat=v.findViewById<EditText>(R.id.latitude); val lon=v.findViewById<EditText>(R.id.longitude); val photo=v.findViewById<ImageView>(R.id.photo)
        photoUri=state?.getString("photo") ?: c.photoUri; photoUri?.let{photo.setImageURI(Uri.parse(it))}
        fun populate(x:Contact){first.setText(x.firstName);last.setText(x.lastName);phone.setText(x.phone);email.setText(x.email);lat.setText(x.latitude?.toString().orEmpty());lon.setText(x.longitude?.toString().orEmpty())}; populate(c)
        fun editable(on:Boolean){listOf(first,last,phone,email,lat,lon).forEach{it.isEnabled=on}; v.findViewById<Button>(R.id.edit).visibility=if(on)View.GONE else View.VISIBLE; v.findViewById<Button>(R.id.save).visibility=if(on)View.VISIBLE else View.GONE; v.findViewById<Button>(R.id.cancel).visibility=if(on)View.VISIBLE else View.GONE; v.findViewById<Button>(R.id.gallery).visibility=if(on)View.VISIBLE else View.GONE; v.findViewById<Button>(R.id.camera).visibility=if(on)View.VISIBLE else View.GONE}
        editable(false); v.findViewById<Button>(R.id.edit).setOnClickListener{editable(true)}
        v.findViewById<Button>(R.id.cancel).setOnClickListener{populate(c);photoUri=c.photoUri;photoUri?.let{photo.setImageURI(Uri.parse(it))};editable(false)}
        v.findViewById<Button>(R.id.save).setOnClickListener{
            val a=lat.text.toString().trim().toDoubleOrNull(); val o=lon.text.toString().trim().toDoubleOrNull(); val la=lat.text.toString().isBlank(); val lo=lon.text.toString().isBlank()
            when { first.text.isBlank()||last.text.isBlank()||phone.text.isBlank()->Toast.makeText(requireContext(),"Prénom, nom et téléphone sont obligatoires.",Toast.LENGTH_LONG).show(); email.text.isNotBlank()&&!android.util.Patterns.EMAIL_ADDRESS.matcher(email.text.toString()).matches()->Toast.makeText(requireContext(),"Adresse e-mail invalide.",Toast.LENGTH_LONG).show(); la!=lo -> Toast.makeText(requireContext(),"Renseignez les deux coordonnées GPS ou aucune.",Toast.LENGTH_LONG).show(); !la&&(a==null||o==null||a !in -90.0..90.0||o !in -180.0..180.0)->Toast.makeText(requireContext(),"Coordonnées GPS invalides.",Toast.LENGTH_LONG).show(); else -> { model.update(c.copy(firstName=first.text.toString().trim(),lastName=last.text.toString().trim(),phone=phone.text.toString().trim(),email=email.text.toString().trim(),photoUri=photoUri,latitude=a,longitude=o)); Toast.makeText(requireContext(),"Contact enregistré",Toast.LENGTH_SHORT).show(); editable(false) } }
        }
        v.findViewById<Button>(R.id.gallery).setOnClickListener{gallery.launch("image/*")}
        v.findViewById<Button>(R.id.camera).setOnClickListener{runCatching { val dir=File(requireContext().cacheDir,"camera").apply{mkdirs()}; val file=File.createTempFile("contact_", ".jpg",dir); cameraUri=FileProvider.getUriForFile(requireContext(),"${requireContext().packageName}.fileprovider",file); camera.launch(cameraUri!!) }.onFailure{Toast.makeText(requireContext(),"Caméra indisponible.",Toast.LENGTH_SHORT).show()} }
        v.findViewById<Button>(R.id.call).setOnClickListener{launch(Intent(Intent.ACTION_DIAL,Uri.parse("tel:${c.phone}")))}
        v.findViewById<Button>(R.id.mail).setOnClickListener{launch(Intent(Intent.ACTION_SENDTO,Uri.parse("mailto:${c.email}")))}
        v.findViewById<Button>(R.id.map).setOnClickListener{parentFragmentManager.beginTransaction().replace(R.id.fragment_container,CarteContactFragment.newInstance(c.id)).addToBackStack("map").commit()}
    }
    private fun launch(i:Intent){try{startActivity(i)}catch(_:Exception){Toast.makeText(requireContext(),"Aucune application disponible pour cette action.",Toast.LENGTH_LONG).show()}}
    override fun onSaveInstanceState(out:Bundle){out.putString("photo",photoUri);super.onSaveInstanceState(out)}
    companion object {private const val ARG_ID="contact_id"; fun newInstance(id:String)=DetailContactFragment().apply{arguments=Bundle().apply{putString(ARG_ID,id)}}}
}
