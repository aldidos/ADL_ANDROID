package com.adl.project.ui.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import com.adl.project.R
import com.adl.project.common.*
import com.adl.project.common.enum.TransitionMode
import com.adl.project.common.util.TimeAxisValueFormat
import com.adl.project.databinding.ActivityMainLineBinding
import com.adl.project.model.adl.AdlListModel
import com.adl.project.model.adl.DeviceListModel
import com.adl.project.service.HttpService
import com.adl.project.ui.base.BaseActivity
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.google.gson.Gson
import kotlinx.coroutines.*
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


/**
 * ADL_MONITORING_APP by CSOS PROJECT
 * DEVELOPER : 한병하 (Glacier Han)
 * TODO :: 메인 실시간 그래프 화면
 */

class MainLineActivity :
    BaseActivity<ActivityMainLineBinding>(ActivityMainLineBinding::inflate, TransitionMode.FADE),
    View.OnClickListener {

    var adlList : AdlListModel? = null
    var deviceList : DeviceListModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        CoroutineScope(Dispatchers.Main).launch {
            connectToServer()
        }
        setInitialize()
    }

    private fun setInitialize() {
        //TODO -- mpchart, 범례 여러개 사용해서, 각 범례마다 y값 지정 (ex 0,10,20,30,40) 그리고 dot&line graph에서 line 제거해서 표현
//        setData(binding.mainChart)
        binding.btnAnal.setOnClickListener(this@MainLineActivity)
        binding.btnSetting.setOnClickListener(this@MainLineActivity)
    }

    /* TODO :: 서버 연결 ->
     *  1. 디바이스 정보 받아와서 축 요소 세팅
     *  2. ADL 정보 받아와서 차트 그리기 */

    private suspend fun getDevice(){
        val URL1 = "http://155.230.186.66:8000/devices/"
        val SLIMHUB = "AB001309"
        val server1 = HttpService.create(URL1 + SLIMHUB + "/")
        val data = server1.getDeviceData()
        Log.d("DBG:RETRO", data)
        deviceList = Gson().fromJson(data, DeviceListModel::class.java)
    }

    private suspend fun getAdl(){
        val URL2 = "http://155.230.186.66:8000/ADLs/"
        val SLIMHUB = "AB001309"
        val server2 = HttpService.create(URL2 + SLIMHUB + "/")
        val data = server2.getMainData("2022-12-17", "2022-12-18")
        Log.d("DBG::RETRO", data)
        adlList = Gson().fromJson(data, AdlListModel::class.java)
    }

    suspend fun connectToServer() {
        // TODO :: 코루틴 도입 -> getDevice, getAdl 을 UI쓰레드에서 분리시키고, 서버연동 과정이 끝나면 차트 그리기. 서버와 연결이 불가능하면 안내문구 띄운 후 앱 종료.
        val job = CoroutineScope(Dispatchers.IO).launch {
            try {
                getDevice()
                getAdl()
            } catch (e:Exception){
                val handler = Handler(Looper.getMainLooper())
                handler.postDelayed(java.lang.Runnable { Toast.makeText(applicationContext,"서버와 연결이 불안정해 앱을 종료합니다.", Toast.LENGTH_LONG).show() }, 0)
                finish()
            }
        }

        runBlocking {
            job.join()
            // job이 끝나면, 밑에 코드 실행

            // TODO :: 축 설정
            setAxisWithData()
        }

    }

    private fun setAxisWithData(){
        Log.d("DBG:RETRO", deviceList.toString())
        Log.d("DBG:RETRO", adlList.toString())

        deviceList?.apply {
            Log.d("DBG:DATA", data.toString())

            val linedataList : ArrayList<LineDataSet> = ArrayList()

            for(d in data.indices){
                val deviceType = data[d].type
                val entryList : ArrayList<Entry> = ArrayList()

                // 각 y축을 그리기 위해, x축 0위치에 투명한 circle을 그린다.
                entryList.add(Entry(0f, d * 10f, AppCompatResources.getDrawable(applicationContext, android.R.color.transparent)))

                // 각 라벨리스트를 순회하며 Adl 수신 값중에 해당하는 type이 있는지 찾는다.
                adlList?.apply {
                    for(d_ in data){
                        // Adl 수신정보 중 타입이 일치하는 값이 있으면 해당값을 entryList에 추가한다
                        if(deviceType == d_.type){
                            // ON/OFF 밸류에 따라 아이콘을 따로 처리해야하기 때문에 분기한다.
                            when (d_.value) {
                                "ON" -> entryList.add(Entry(convertTimeToMin(timestampToTime(d_.time)), d * 10f, AppCompatResources.getDrawable(applicationContext, R.drawable.ic_baseline_arrow_drop_up_24)))
                                "OFF" -> entryList.add(Entry(convertTimeToMin(timestampToTime(d_.time)), d * 10f, AppCompatResources.getDrawable(applicationContext, R.drawable.ic_baseline_arrow_drop_down_24)))
                            }
                        }
                    }
                }
                val linedata = LineDataSet(entryList, deviceType)
                linedata.lineWidth = 0f
                linedata.setDrawValues(false)
                linedata.setDrawCircles(false)
                linedataList.add(linedata)
            }

            Log.d("DBG:LINE", linedataList.toString())

            val dataSet: ArrayList<ILineDataSet> = ArrayList()
            for(ld in linedataList){
                dataSet.add(ld)
            }
            dataSet.reverse()

            // 모든 과정이 끝나면 차트 그리기
            setData(binding.mainChart, dataSet)
        }
    }

    private fun setData(chart: LineChart, dataSet: ArrayList<ILineDataSet>) {
//        val entries_0_on = ArrayList<Entry>()
//        entries_0_on.add(Entry(convertTimeToMin("10:44:23"), 0.0f, AppCompatResources.getDrawable(applicationContext, R.drawable.ic_baseline_arrow_drop_up_24)))
//        entries_0_on.add(Entry(convertTimeToMin("13:44:23"), 0.0f, AppCompatResources.getDrawable(applicationContext, R.drawable.ic_baseline_arrow_drop_down_24)))
//
//        val entries_1_on = ArrayList<Entry>()
//
//        val entries2 = ArrayList<Entry>()
//        entries2.add(Entry(1.4f, 10.0f))
//        entries2.add(Entry(3.4f, 10.0f))
//        entries2.add(Entry(3.6f, 10.0f))
//        entries2.add(Entry(4.2f, 10.0f))
//
//        var set = LineDataSet(entries_0_on, "환경 경보") // 데이터셋 초기화
//        var set0 = LineDataSet(entries_1_on, "환경 경보") // 데이터셋 초기화
//
//        var set2 = LineDataSet(entries2, "전자렌지") // 데이터셋 초기화
//        Log.d("DATA", "setted")
//
//        set.lineWidth = 0f
//        set.setDrawValues(false)
//        set2.lineWidth = 0f
//        set2.setDrawValues(false)
//
//        val dataSet: ArrayList<ILineDataSet> = ArrayList()
//        dataSet.add(set)
//        dataSet.add(set0)
//        dataSet.add(set2)
//
//        dataSet.reverse()

        val data = LineData(dataSet)
        chart.run {
            description.isEnabled = true // 차트 옆에 별도로 표기되는 description을 안보이게 설정 (false)
            setPinchZoom(false) // 핀치줌(두손가락으로 줌인 줌 아웃하는것) 설정
            setDrawGridBackground(false)//격자구조 넣을건지
            setTouchEnabled(true) // 그래프 터치해도 아무 변화없게 막음
            animateY(1000) // 밑에서부터 올라오는 애니매이션 적용

            axisRight.isEnabled = false // 오른쪽 Y축을 안보이게 해줌.

            axisLeft.run { //왼쪽 축. 즉 Y방향 축을 뜻한다.
                // setDrawBarShadow(false) //그래프의 그림자
                // axisLineColor = ContextCompat.getColor(context,R.color.design_default_color_secondary_variant) // 축 색깔 설정
                // gridColor = ContextCompat.getColor(context,R.color.design_default_color_on_secondary) // 축 아닌 격자 색깔 설정
                // textColor = ContextCompat.getColor(context,R.color.design_default_color_primary_dark) // 라벨 텍스트 컬러 설정
                // axisMaximum = 30f //100 위치에 선을 그리기 위해 101f로 맥시멈값 설정
                // axisMinimum = 0f // 최소값 0
                granularity = 10f // 50 단위마다 선을 그리려고 설정
                setDrawLabels(true) // 값 적는거 허용 (0, 50, 100)
                setDrawGridLines(true) //격자 라인 활용
                setDrawAxisLine(false) // 축 그리기 설정
                textSize = 13f //라벨 텍스트 크기
            }

            xAxis.run {
                // textColor = ContextCompat.getColor(context,R.color.design_default_color_primary_dark) //라벨 색상
                position = XAxis.XAxisPosition.BOTTOM //X축을 아래에다가 둔다.
                granularity = 0.1f // 1 단위만큼 간격 두기
                setDrawAxisLine(true) // 축 그림
                setDrawGridLines(false) // 격자
                textSize = 12f // 텍스트 크기
                xAxis.valueFormatter = TimeAxisValueFormat()
                xAxis.setDrawLabels(true)  // Label 표시 여부
                xAxis.axisMinimum = 0f  // -240f : 오전 5시, 0f : 오전 9시
                xAxis.axisMaximum = 1440f
            }

            legend.run {
                isEnabled = false //차트 범례 설정
                horizontalAlignment = Legend.LegendHorizontalAlignment.LEFT
                verticalAlignment = Legend.LegendVerticalAlignment.CENTER
                orientation = Legend.LegendOrientation.VERTICAL
                formSize = 20f
                yEntrySpace = 50f
                xOffset = 20f
                setDrawInside(false)
            }

            this.data = data //차트의 데이터를 data로 설정해줌.
            invalidate()
            setMaxVisibleValueCount(10000)
        }
    }

    private fun convertTimeToMin(value: String): Float {
        // HH:mm:ss
        var timeMin = 0.0
        try {
            timeMin += value.split(":")[1].toInt() //분
            timeMin += value.split(":")[0].toInt() * 60 //시
            timeMin += value.split(":")[2].toInt() / 60.0

        } catch (e: Exception) {
            e.printStackTrace()
        }

        // 오전 9시가 0이 되어야하는 상황
        timeMin -= 540f
        // 오전 7시 처리
        if (timeMin < 0) {
            timeMin = 1440f + timeMin
        }
//        Log.d("time", timeMin.toString())

        return timeMin.toFloat()// 오전9시 기준이기 때문에 540빼줌
    }

    private fun timestampToTime(timestamp: Timestamp) : String{
        val time = timestamp.time
        val res = SimpleDateFormat("hh:mm:ss", Locale.KOREA).format(Date(time))
        Log.d("DBG::TIME", res.toString())
        return res
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.btn_anal -> {
                val intent = Intent(applicationContext, AnalyticActivity::class.java)
                intent.putExtra("name", "냉장고")
                intent.putExtra("mode", 1)
                startActivity(intent)
            }
            R.id.btn_setting -> {
                val intent = Intent(applicationContext, SettingActivity::class.java)
                startActivity(intent)
            }
        }
    }

}