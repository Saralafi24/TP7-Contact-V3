package fr.tp.contacts

import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ListeContactsFragment : Fragment() {
    private val model: ContactsViewModel by activityViewModels()
    private lateinit var adapter: ContactAdapter
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, state: Bundle?) = inflater.inflate(R.layout.fragment_list, container, false)
    override fun onViewCreated(view: View, state: Bundle?) {
        val recycler = view.findViewById<RecyclerView>(R.id.recycler); val empty = view.findViewById<TextView>(R.id.empty)
        adapter = ContactAdapter { id -> parentFragmentManager.beginTransaction().replace(R.id.fragment_container, DetailContactFragment.newInstance(id)).addToBackStack("detail").commit() }
        recycler.layoutManager = LinearLayoutManager(requireContext()); recycler.adapter = adapter
        val search = view.findViewById<EditText>(R.id.search)
        model.contacts.observe(viewLifecycleOwner) { all -> adapter.submit(all.filter { c -> val q=search.text.toString().trim(); q.isEmpty() || listOf(c.firstName,c.lastName,c.phone).any { it.contains(q, true) } }); empty.visibility=if(adapter.itemCount==0) View.VISIBLE else View.GONE }
        search.addTextChangedListener(object: android.text.TextWatcher { override fun beforeTextChanged(s: CharSequence?, start:Int,count:Int,after:Int){}; override fun onTextChanged(s:CharSequence?,start:Int,before:Int,count:Int){ val q=s.toString().trim(); adapter.submit(model.contacts.value.orEmpty().filter { c -> q.isEmpty() || listOf(c.firstName,c.lastName,c.phone).any { it.contains(q,true) } }); empty.visibility=if(adapter.itemCount==0) View.VISIBLE else View.GONE }; override fun afterTextChanged(s:android.text.Editable?){} })
    }
}

private class ContactAdapter(val click:(String)->Unit) : RecyclerView.Adapter<ContactAdapter.Holder>() {
    private var items=listOf<Contact>(); fun submit(value:List<Contact>){items=value; notifyDataSetChanged()}
    override fun onCreateViewHolder(parent:ViewGroup,viewType:Int)=Holder(LayoutInflater.from(parent.context).inflate(R.layout.row_contact,parent,false),click)
    override fun getItemCount()=items.size
    override fun onBindViewHolder(holder:Holder,position:Int){holder.bind(items[position])}
    class Holder(v:View,val click:(String)->Unit):RecyclerView.ViewHolder(v){ fun bind(c:Contact){ itemView.findViewById<TextView>(R.id.name).text=c.displayName; itemView.findViewById<TextView>(R.id.phone).text=c.phone; val image=itemView.findViewById<ImageView>(R.id.photo); image.setImageResource(android.R.drawable.ic_menu_myplaces); c.photoUri?.let { runCatching { image.setImageURI(android.net.Uri.parse(it)) } }; itemView.setOnClickListener{click(c.id)} } }
}
