package br.edu.puc.sorriso24h.views

import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.renderscript.ScriptGroup.Input
import android.text.InputType
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import br.edu.puc.sorriso24h.R
import br.edu.puc.sorriso24h.databinding.ActivityAccountDetailsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import br.edu.puc.sorriso24h.infra.Constants
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.storage.FirebaseStorage
import org.w3c.dom.Text
import java.io.File

class AccountDetailsActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding : ActivityAccountDetailsBinding

    private lateinit var auth : FirebaseAuth
    private lateinit var db : FirebaseFirestore
    private lateinit var messaging : FirebaseMessaging
    private lateinit var storage : FirebaseStorage

    private lateinit var end1List :List<String>
    private lateinit var end2List :List<String>
    private lateinit var end3List :List<String>

    private lateinit var attField : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAccountDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        messaging = FirebaseMessaging.getInstance()
        storage = FirebaseStorage.getInstance()

        binding.imageArrowBack.setColorFilter(ContextCompat.getColor(this, R.color.purple_500))

        binding.btnVoltarRegister.setOnClickListener(this)
        binding.textAddress1.setOnClickListener(this)
        binding.textAddress2.setOnClickListener(this)
        binding.textAddress3.setOnClickListener(this)
        binding.imageButtonEditName.setOnClickListener(this)
        binding.imageButtonEditEmail.setOnClickListener(this)
        binding.imageButtonEditTelefone.setOnClickListener(this)
        binding.buttonAtt.setOnClickListener(this)
        binding.buttonCancelarAtt.setOnClickListener(this)

        setInfos()
    }

    private fun setInfos(){
        val file : File = File.createTempFile("tempfile", ".jpg")
        storage.getReference("images_user/${auth.currentUser!!.uid}").getFile(file).addOnSuccessListener {
            binding.imagePhoto.setBackgroundColor(resources.getColor(R.color.gray))
            binding.imagePhoto.setImageBitmap(BitmapFactory.decodeFile(file.absolutePath))
        }
        db.collection(Constants.DB.DENTISTAS)
            .whereEqualTo("uid", auth.currentUser!!.uid)
            .get()
            .addOnCompleteListener {
                doc ->
                binding.textAccountDetailNome.text = doc.result.documents[0].get(Constants.DB.FIELD.NAME_DB).toString()
                binding.textAccountDetailEmail.text = doc.result.documents[0].get(Constants.DB.FIELD.EMAIL_DB).toString()
                binding.textAccountDetailTelefone.text = doc.result.documents[0].get(Constants.DB.FIELD.PHONE).toString()

                end1List = doc.result.documents[0].get("endereco").toString().split(',')
                binding.textAddress1.text = end1List[0]

                try {
                    end2List = doc.result.documents[0].get("endereco_2").toString().split(',')
                    if(end2List[0] != "null"){
                        binding.textAddress2.text = end2List[0]
                    }
                }catch (_:Exception){}
                try {
                    end3List = doc.result.documents[0].get("endereco_3").toString().split(',')
                    if (end3List[0] != "null") {
                        binding.textAddress3.text = end3List[0]
                    }
                }catch (_:Exception){}
                setAddress(Constants.KEY_SHARED.ADDRESS_1_REGISTER)

                binding.progressMain.visibility = View.INVISIBLE
            }
    }
    private fun setAddress(addrees : String){
        if (addrees == Constants.KEY_SHARED.ADDRESS_1_REGISTER && binding.textAddress1.text != "Endereço 1"){
            binding.textAddress1.setTextColor(resources.getColor(R.color.purple_500))
            binding.textAddress1.setBackgroundColor(resources.getColor(R.color.gray))
            binding.textAccountDetailStreet.text = end1List[1]
            binding.textAccountDetailNumber.text = end1List[2]
            binding.textAccountDetailNeighborhood.text = end1List[3]
            binding.textAccountDetailCEP.text = end1List[4]
            binding.textAccountDetailCity.text = end1List[5]
            binding.textAccountDetailState.text = end1List[6]

            binding.textAddress2.setTextColor(resources.getColor(R.color.white))
            binding.textAddress3.setTextColor(resources.getColor(R.color.white))
            binding.textAddress2.setBackgroundColor(resources.getColor(R.color.purple_500))
            binding.textAddress3.setBackgroundColor(resources.getColor(R.color.purple_500))
        }
        else if (addrees == Constants.KEY_SHARED.ADDRESS_2_REGISTER) {
            if (binding.textAddress2.text.toString() != "Endereço 2") {
                binding.textAddress2.setTextColor(resources.getColor(R.color.purple_500))
                binding.textAddress2.setBackgroundColor(resources.getColor(R.color.gray))
                binding.textAccountDetailStreet.text = end2List[1]
                binding.textAccountDetailNumber.text = end2List[2]
                binding.textAccountDetailNeighborhood.text = end2List[3]
                binding.textAccountDetailCEP.text = end2List[4]
                binding.textAccountDetailCity.text = end2List[5]
                binding.textAccountDetailState.text = end2List[6]

                binding.textAddress1.setTextColor(resources.getColor(R.color.white))
                binding.textAddress3.setTextColor(resources.getColor(R.color.white))
                binding.textAddress1.setBackgroundColor(resources.getColor(R.color.purple_500))
                binding.textAddress3.setBackgroundColor(resources.getColor(R.color.purple_500))
            }else {
                Snackbar.make(binding.root, "Não possui um segundo endereço registrado!", Snackbar.LENGTH_LONG)
                    .setBackgroundTint(Color.RED)
                    .show()
                return
            }
        }
        else if (addrees == Constants.KEY_SHARED.ADDRESS_3_REGISTER) {
            if (binding.textAddress3.text.toString() != "Endereço 3") {
                binding.textAddress3.setTextColor(resources.getColor(R.color.purple_500))
                binding.textAddress3.setBackgroundColor(resources.getColor(R.color.gray))
                binding.textAccountDetailStreet.text = end3List[1]
                binding.textAccountDetailNumber.text = end3List[2]
                binding.textAccountDetailNeighborhood.text = end3List[3]
                binding.textAccountDetailCEP.text = end3List[4]
                binding.textAccountDetailCity.text = end3List[5]
                binding.textAccountDetailState.text = end3List[6]

                binding.textAddress1.setTextColor(resources.getColor(R.color.white))
                binding.textAddress2.setTextColor(resources.getColor(R.color.white))
                binding.textAddress1.setBackgroundColor(resources.getColor(R.color.purple_500))
                binding.textAddress2.setBackgroundColor(resources.getColor(R.color.purple_500))
            }else {
                Snackbar.make(binding.root, "Não possui um terceiro endereço registrado!", Snackbar.LENGTH_LONG)
                    .setBackgroundTint(Color.RED)
                    .show()
                return
            }
        }
    }
    private fun openEditAtt(field: String){
        binding.editAtt.hint = "Novo $field"

        attField = field

        when (field) {
            Constants.DB.FIELD.NAME_DB -> {
                binding.editAtt.inputType = InputType.TYPE_CLASS_TEXT
            }
            Constants.DB.FIELD.EMAIL_DB -> {
                binding.editAtt.inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
            }
            Constants.DB.FIELD.PHONE -> {
                binding.editAtt.inputType = InputType.TYPE_CLASS_NUMBER
            }
        }

        binding.editAtt.visibility = View.VISIBLE
        binding.buttonAtt.visibility = View.VISIBLE
        binding.buttonCancelarAtt.visibility = View.VISIBLE

        binding.textAccountDetailNome.visibility = View.INVISIBLE
        binding.textAccountDetailEmail.visibility = View.INVISIBLE
        binding.textAccountDetailTelefone.visibility = View.INVISIBLE
        binding.textView13.visibility = View.INVISIBLE
        binding.textView14.visibility = View.INVISIBLE
        binding.textView15.visibility = View.INVISIBLE
        binding.imageButtonEditName.visibility = View.INVISIBLE
        binding.imageButtonEditEmail.visibility = View.INVISIBLE
        binding.imageButtonEditTelefone.visibility = View.INVISIBLE
    }
    private fun closeEditAtt() {
        binding.editAtt.setText("")

        binding.editAtt.visibility = View.INVISIBLE
        binding.buttonAtt.visibility = View.INVISIBLE
        binding.buttonCancelarAtt.visibility = View.INVISIBLE

        binding.textAccountDetailNome.visibility = View.VISIBLE
        binding.textAccountDetailEmail.visibility = View.VISIBLE
        binding.textAccountDetailTelefone.visibility = View.VISIBLE
        binding.textView13.visibility = View.VISIBLE
        binding.textView14.visibility = View.VISIBLE
        binding.textView15.visibility = View.VISIBLE
        binding.imageButtonEditName.visibility = View.VISIBLE
        binding.imageButtonEditEmail.visibility = View.VISIBLE
        binding.imageButtonEditTelefone.visibility = View.VISIBLE
    }
    private fun updateField(field: String){
        if (binding.editAtt.text.trim().isEmpty()) {
            binding.editAtt.error = Constants.PHRASE.EMPTY_FIELD
            return
        }
        val dados = hashMapOf(
            field to binding.editAtt.text.toString().trim()
        )

        db.collection(Constants.DB.DENTISTAS)
            .whereEqualTo(Constants.DB.FIELD.UID, auth.currentUser!!.uid)
            .get()
            .addOnCompleteListener {
                val doc : DocumentSnapshot = it.result.documents[0]
                val docId : String = doc.id

                db.collection(Constants.DB.DENTISTAS)
                    .document(docId)
                    .update(dados as Map<String, Any>)
                    .addOnCompleteListener {
                        closeEditAtt()
                        setInfos()
                        Snackbar.make(binding.root, "$field atualizado com sucesso!".uppercase(), Snackbar.LENGTH_LONG)
                            .setBackgroundTint(Color.GREEN)
                            .show()
                    }
            }
    }
    override fun onClick(v: View) {
        when(v.id) {
            R.id.btn_voltar_register -> {
                startActivity(Intent(this, UserActivity::class.java))
                finish()
            }
            R.id.text_address1 -> setAddress(Constants.KEY_SHARED.ADDRESS_1_REGISTER)
            R.id.text_address2 -> setAddress(Constants.KEY_SHARED.ADDRESS_2_REGISTER)
            R.id.text_address3 -> setAddress(Constants.KEY_SHARED.ADDRESS_3_REGISTER)
            R.id.imageButton_editName -> openEditAtt(Constants.DB.FIELD.NAME_DB)
            R.id.imageButton_editEmail -> openEditAtt(Constants.DB.FIELD.EMAIL_DB)
            R.id.imageButton_editTelefone -> openEditAtt(Constants.DB.FIELD.PHONE)
            R.id.button_att -> updateField(attField)
            R.id.button_cancelarAtt -> closeEditAtt()
        }
    }
}