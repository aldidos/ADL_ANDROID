package com.adl.project.ui.activity

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import com.adl.project.R
import com.adl.project.common.enum.TransitionMode
import com.adl.project.databinding.ActivityAnalyticBinding
import com.adl.project.ui.base.BaseActivity
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate

/**
 * ADL_MONITORING_APP by CSOS PROJECT
 * DEVELOPER : 한병하 (Glacier Han)
 * TODO :: 분석 결과 메뉴화면
 */

class AnalyticActivity :
    BaseActivity<ActivityAnalyticBinding>(ActivityAnalyticBinding::inflate, TransitionMode.FADE),
    View.OnClickListener {

    var mode = 0
    var name = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getIntents()
        setInitialize()
    }

    fun setInitialize() {
//        binding.tvTest.setText(name + " > " + mode)
        setChart1()
        setChart2()

        binding.btn1.setOnClickListener(this@AnalyticActivity)
        binding.btn2.setOnClickListener(this@AnalyticActivity)
        binding.btn3.setOnClickListener(this@AnalyticActivity)
        binding.btn4.setOnClickListener(this@AnalyticActivity)
        binding.btn5.setOnClickListener(this@AnalyticActivity)

    }

    fun setChart1(){
        val chart =  binding.piechart1
        chart.setUsePercentValues(true)

        // data Set
        val entries = ArrayList<PieEntry>()
        entries.add(PieEntry(508f, "수면"))
        entries.add(PieEntry(600f, "요리"))
        entries.add(PieEntry(750f, "활동"))
        entries.add(PieEntry(508f, "씻기"))
        entries.add(PieEntry(670f, "식사"))

        val pieDataSet = PieDataSet(entries, "")
        pieDataSet.apply {
            valueTextColor = Color.BLACK
            colors = ColorTemplate.createColors(ColorTemplate.JOYFUL_COLORS)
            valueTextSize = 16f
        }

        val pieData = PieData(pieDataSet)
        chart.apply {
            data = pieData
            description.isEnabled = false
            isRotationEnabled = false
            centerText = "일상생활\n행동비율"
            setEntryLabelColor(Color.BLACK)
            animateY(1000, Easing.EaseInOutQuad)
            animate()
            legend.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
        }
    }

    fun setChart2(){
        val chart =  binding.piechart2
        chart.setUsePercentValues(true)

        // data Set
        val entries = ArrayList<PieEntry>()
        entries.add(PieEntry(508f, "수면"))
        entries.add(PieEntry(600f, "요리"))
        entries.add(PieEntry(750f, "활동"))
        entries.add(PieEntry(508f, "씻기"))
        entries.add(PieEntry(670f, "식사"))

        val pieDataSet = PieDataSet(entries, "")
        pieDataSet.apply {
            valueTextColor = Color.BLACK
            colors = ColorTemplate.createColors(ColorTemplate.PASTEL_COLORS)
            valueTextSize = 16f
        }

        val pieData = PieData(pieDataSet)
        chart.apply {
            data = pieData
            description.isEnabled = false
            isRotationEnabled = false
            centerText = "60대 남성\n평균 행동비율"
            setEntryLabelColor(Color.BLACK)
            animateY(1000, Easing.EaseInOutQuad)
            animate()
            legend.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
        }
    }

    fun getIntents() {
        intent.apply {
            if (extras != null) {
                mode = intent.getIntExtra("mode", 1)
                name = intent.getStringExtra("name").toString()
            } else {
                finish()
            }
        }
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.btn_1 -> {
                val intent = Intent(applicationContext, AnalyticDetailActivity::class.java)
                intent.putExtra("name", "생활능력")
                intent.putExtra("mode", 1)
                startActivity(intent)
            }

            R.id.btn_2 -> {
                val intent = Intent(applicationContext, AnalyticDetailActivity::class.java)
                intent.putExtra("name", "운동능력")
                intent.putExtra("mode", 2)
                startActivity(intent)
            }

            R.id.btn_3 -> {
                val intent = Intent(applicationContext, AnalyticDetailActivity::class.java)
                intent.putExtra("name", "정규성")
                intent.putExtra("mode", 3)
                startActivity(intent)
            }

            R.id.btn_4 -> {
                val intent = Intent(applicationContext, AnalyticDetailActivity::class.java)
                intent.putExtra("name", "다양성")
                intent.putExtra("mode", 4)
                startActivity(intent)
            }

            R.id.btn_5 -> {
                val intent = Intent(applicationContext, AnalyticDetailActivity::class.java)
                intent.putExtra("name", "정확성")
                intent.putExtra("mode", 5)
                startActivity(intent)
            }
        }
    }
}
