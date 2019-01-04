package com.kbu.lib


import android.content.DialogInterface
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.widget.LinearLayout
import android.widget.TextView
import com.bumptech.glide.Glide
import com.kbu.lib.Base.BaseActivity
import com.kbu.lib.Recycler.BookInformation_recycler
import com.kbu.lib.data.Information
import kotlinx.android.synthetic.main.activity_book_information.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.jsoup.Jsoup

class BookInformationActivity : BaseActivity(R.layout.activity_book_information) {


    override fun set() {
        if (!NetWorkch()) {

            val builder = AlertDialog.Builder(this)
            with(builder)
            {
                setTitle("인터넷이 연결되어있지않습니다.")
                setMessage("인터넷이 연결되어 있지 않거나 연결에 문제가 발생하였습니다.\n나중에 다시 시도하시거나 계속 문제가 발생된다면 mookseong147@gmail.com으로 문의해주시기 바랍니다.")
                setPositiveButton("종료", DialogInterface.OnClickListener(function = positiveButtonClick))
                show()
            }
        } else {
            //Toast.makeText(this, intent.getStringExtra("URL"), Toast.LENGTH_SHORT).show()
            val URL: String = intent.getStringExtra("URL")
            val libURL = "http://lib.bible.ac.kr"
            possession_Information.layoutManager = LinearLayoutManager(this, LinearLayout.VERTICAL, false)
            possession_Information.setHasFixedSize(true)

            GlobalScope.launch(Dispatchers.Main) {
                val Datalist = arrayListOf<String>()
                val TextviewId = arrayListOf<TextView>(
                    Title_information,
                    DCC_information,
                    number_information,
                    weiter_information,
                    text_information
                )
                val titlelist = arrayListOf("DDC", "청구기호", "서명/저자", "발행사항")
                val asysnc = GlobalScope.async(Dispatchers.IO) {
                    val DATA_elementstitle =
                        Jsoup.connect(libURL + URL).get().select("div[class=col-md-10 detail-table-right]")
                    val DATA_elementstext = DATA_elementstitle.select("dl")
                    Datalist.add(DATA_elementstitle.select("div[class=sponge-book-title]").text().toString())
                    for (i in DATA_elementstext.indices) {
                        for (j in 0..3) {
                            if (DATA_elementstext[i].select("dt").text().toString() == titlelist[j]) {
                                Datalist.add(DATA_elementstext[i].select("dd").text().toString())
                                //Log.d("TEST$i", DATA_elementstext[i].select("dd").text().toString())
                            }
                        }
                    }
                }.await()
                for (i in Datalist.indices)
                    TextviewId[i].text = Datalist[i]
            }
            GlobalScope.launch(Dispatchers.Main) {
                val possess_information = arrayListOf<Information>()

                val asysnc = GlobalScope.async(Dispatchers.IO) {
                    val possess_information_elements =
                        Jsoup.connect(libURL + URL).get()
                            .select("div[class=sponge-guide-Box-table sponge-detail-table]")
                            .select("table[class=table-striped sponge-table-default]").select("tbody tr")
                    for (i in possess_information_elements.indices) {
                        possess_information.add(
                            Information(
                                possess_information_elements[i].select("td")[0].text().toString(),
                                possess_information_elements[i].select("td")[1].text().toString(),
                                possess_information_elements[i].select("td")[2].text().toString(),
                                possess_information_elements[i].select("td")[3].text().toString()
                            )
                        )
                    }
                    //Log.d("TEST$i", possess_information_elements.select("td")[i].text().toString())
                }.await()
                possession_Information.adapter = BookInformation_recycler(possess_information)
            }
            Glide.with(this).load(intent.getStringExtra("IMG")).into(Img_information)
        }
    }
}
