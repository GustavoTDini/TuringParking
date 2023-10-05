package com.example.turingparking.network

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.turingparking.BuildConfig
import com.google.gson.Gson

class MailHelpers {
    companion object {

        data class Sender(
            val name: String,
            val email: String
        )

        data class To(
            val email: String,
            val name: String
        )

        data class Body(
            val sender: Sender,
            val to: List<To>,
            val subject: String,
            val htmlContent: String
        )

        private const val TAG = "MailHelper"

        private fun setCodeHtml (code: String): String{
            return "<!doctype html><html ⚡4email data-css-strict><head><meta charset=\"utf-8\"><style amp4email-boilerplate>body{visibility:hidden}</style><script async src=\"https://cdn.ampproject.org/v0.js\"></script><style amp-custom>.es-desk-hidden {\tdisplay:none;\tfloat:left;\toverflow:hidden;\twidth:0;\tmax-height:0;\tline-height:0;}body {\twidth:100%;\tfont-family:arial, \"helvetica neue\", helvetica, sans-serif;}table {\tborder-collapse:collapse;\tborder-spacing:0px;}table td, body, .es-wrapper {\tpadding:0;\tMargin:0;}.es-content, .es-header, .es-footer {\ttable-layout:fixed;\twidth:100%;}p, hr {\tMargin:0;}h1, h2, h3, h4, h5 {\tMargin:0;\tline-height:120%;\tfont-family:arial, \"helvetica neue\", helvetica, sans-serif;}.es-left {\tfloat:left;}.es-right {\tfloat:right;}.es-p5 {\tpadding:5px;}.es-p5t {\tpadding-top:5px;}.es-p5b {\tpadding-bottom:5px;}.es-p5l {\tpadding-left:5px;}.es-p5r {\tpadding-right:5px;}.es-p10 {\tpadding:10px;}.es-p10t {\tpadding-top:10px;}.es-p10b {\tpadding-bottom:10px;}.es-p10l {\tpadding-left:10px;}.es-p10r {\tpadding-right:10px;}.es-p15 {\tpadding:15px;}.es-p15t {\tpadding-top:15px;}.es-p15b {\tpadding-bottom:15px;}.es-p15l {\tpadding-left:15px;}.es-p15r {\tpadding-right:15px;}.es-p20 {\tpadding:20px;}.es-p20t {\tpadding-top:20px;}.es-p20b {\tpadding-bottom:20px;}.es-p20l {\tpadding-left:20px;}.es-p20r {\tpadding-right:20px;}.es-p25 {\tpadding:25px;}.es-p25t {\tpadding-top:25px;}.es-p25b {\tpadding-bottom:25px;}.es-p25l {\tpadding-left:25px;}.es-p25r {\tpadding-right:25px;}.es-p30 {\tpadding:30px;}.es-p30t {\tpadding-top:30px;}.es-p30b {\tpadding-bottom:30px;}.es-p30l {\tpadding-left:30px;}.es-p30r {\tpadding-right:30px;}.es-p35 {\tpadding:35px;}.es-p35t {\tpadding-top:35px;}.es-p35b {\tpadding-bottom:35px;}.es-p35l {\tpadding-left:35px;}.es-p35r {\tpadding-right:35px;}.es-p40 {\tpadding:40px;}.es-p40t {\tpadding-top:40px;}.es-p40b {\tpadding-bottom:40px;}.es-p40l {\tpadding-left:40px;}.es-p40r {\tpadding-right:40px;}.es-menu td {\tborder:0;}s {\ttext-decoration:line-through;}p, ul li, ol li {\tfont-family:arial, \"helvetica neue\", helvetica, sans-serif;\tline-height:150%;}ul li, ol li {\tMargin-bottom:15px;\tmargin-left:0;}a {\ttext-decoration:underline;}.es-menu td a {\ttext-decoration:none;\tdisplay:block;\tfont-family:arial, \"helvetica neue\", helvetica, sans-serif;}.es-wrapper {\twidth:100%;\theight:100%;}.es-wrapper-color, .es-wrapper {\tbackground-color:#FAFAFA;}.es-header {\tbackground-color:transparent;}.es-header-body {\tbackground-color:transparent;}.es-header-body p, .es-header-body ul li, .es-header-body ol li {\tcolor:#333333;\tfont-size:14px;}.es-header-body a {\tcolor:#666666;\tfont-size:14px;}.es-content-body {\tbackground-color:#FFFFFF;}.es-content-body p, .es-content-body ul li, .es-content-body ol li {\tcolor:#333333;\tfont-size:14px;}.es-content-body a {\tcolor:#5C68E2;\tfont-size:14px;}.es-footer {\tbackground-color:transparent;}.es-footer-body {\tbackground-color:#FFFFFF;}.es-footer-body p, .es-footer-body ul li, .es-footer-body ol li {\tcolor:#333333;\tfont-size:12px;}.es-footer-body a {\tcolor:#333333;\tfont-size:12px;}.es-infoblock, .es-infoblock p, .es-infoblock ul li, .es-infoblock ol li {\tline-height:120%;\tfont-size:12px;\tcolor:#CCCCCC;}.es-infoblock a {\tfont-size:12px;\tcolor:#CCCCCC;}h1 {\tfont-size:46px;\tfont-style:normal;\tfont-weight:bold;\tcolor:#333333;}h2 {\tfont-size:26px;\tfont-style:normal;\tfont-weight:bold;\tcolor:#333333;}h3 {\tfont-size:20px;\tfont-style:normal;\tfont-weight:bold;\tcolor:#333333;}.es-header-body h1 a, .es-content-body h1 a, .es-footer-body h1 a {\tfont-size:46px;}.es-header-body h2 a, .es-content-body h2 a, .es-footer-body h2 a {\tfont-size:26px;}.es-header-body h3 a, .es-content-body h3 a, .es-footer-body h3 a {\tfont-size:20px;}a.es-button, button.es-button {\tpadding:10px 30px 10px 30px;\tdisplay:inline-block;\tbackground:#5C68E2;\tborder-radius:0px;\tfont-size:20px;\tfont-family:arial, \"helvetica neue\", helvetica, sans-serif;\tfont-weight:normal;\tfont-style:normal;\tline-height:120%;\tcolor:#FFFFFF;\ttext-decoration:none;\twidth:auto;\ttext-align:center;}.es-button-border {\tborder-style:solid solid solid solid;\tborder-color:#2CB543 #2CB543 #2CB543 #2CB543;\tbackground:#5C68E2;\tborder-width:0px 0px 0px 0px;\tdisplay:inline-block;\tborder-radius:0px;\twidth:auto;}.es-menu amp-img, .es-button amp-img {\tvertical-align:middle;}@media only screen and (max-width:600px) {p, ul li, ol li, a { line-height:150% } h1, h2, h3, h1 a, h2 a, h3 a { line-height:120% } h1 { font-size:36px; text-align:left } h2 { font-size:26px; text-align:left } h3 { font-size:20px; text-align:left } .es-header-body h1 a, .es-content-body h1 a, .es-footer-body h1 a { font-size:36px; text-align:left } .es-header-body h2 a, .es-content-body h2 a, .es-footer-body h2 a { font-size:26px; text-align:left } .es-header-body h3 a, .es-content-body h3 a, .es-footer-body h3 a { font-size:20px; text-align:left } .es-menu td a { font-size:12px } .es-header-body p, .es-header-body ul li, .es-header-body ol li, .es-header-body a { font-size:14px } .es-content-body p, .es-content-body ul li, .es-content-body ol li, .es-content-body a { font-size:16px } .es-footer-body p, .es-footer-body ul li, .es-footer-body ol li, .es-footer-body a { font-size:14px } .es-infoblock p, .es-infoblock ul li, .es-infoblock ol li, .es-infoblock a { font-size:12px } *[class=\"gmail-fix\"] { display:none } .es-m-txt-c, .es-m-txt-c h1, .es-m-txt-c h2, .es-m-txt-c h3 { text-align:center } .es-m-txt-r, .es-m-txt-r h1, .es-m-txt-r h2, .es-m-txt-r h3 { text-align:right } .es-m-txt-l, .es-m-txt-l h1, .es-m-txt-l h2, .es-m-txt-l h3 { text-align:left } .es-m-txt-r amp-img { float:right } .es-m-txt-c amp-img { margin:0 auto } .es-m-txt-l amp-img { float:left } .es-button-border { display:inline-block } a.es-button, button.es-button { font-size:20px; display:inline-block } .es-adaptive table, .es-left, .es-right { width:100% } .es-content table, .es-header table, .es-footer table, .es-content, .es-footer, .es-header { width:100%; max-width:600px } .es-adapt-td { display:block; width:100% } .adapt-img { width:100%; height:auto } td.es-m-p0 { padding:0 } td.es-m-p0r { padding-right:0 } td.es-m-p0l { padding-left:0 } td.es-m-p0t { padding-top:0 } td.es-m-p0b { padding-bottom:0 } td.es-m-p20b { padding-bottom:20px } .es-mobile-hidden, .es-hidden { display:none } tr.es-desk-hidden, td.es-desk-hidden, table.es-desk-hidden { width:auto; overflow:visible; float:none; max-height:inherit; line-height:inherit } tr.es-desk-hidden { display:table-row } table.es-desk-hidden { display:table } td.es-desk-menu-hidden { display:table-cell } .es-menu td { width:1% } table.es-table-not-adapt, .esd-block-html table { width:auto } table.es-social { display:inline-block } table.es-social td { display:inline-block } td.es-m-p5 { padding:5px } td.es-m-p5t { padding-top:5px } td.es-m-p5b { padding-bottom:5px } td.es-m-p5r { padding-right:5px } td.es-m-p5l { padding-left:5px } td.es-m-p10 { padding:10px } td.es-m-p10t { padding-top:10px } td.es-m-p10b { padding-bottom:10px } td.es-m-p10r { padding-right:10px } td.es-m-p10l { padding-left:10px } td.es-m-p15 { padding:15px } td.es-m-p15t { padding-top:15px } td.es-m-p15b { padding-bottom:15px } td.es-m-p15r { padding-right:15px } td.es-m-p15l { padding-left:15px } td.es-m-p20 { padding:20px } td.es-m-p20t { padding-top:20px } td.es-m-p20r { padding-right:20px } td.es-m-p20l { padding-left:20px } td.es-m-p25 { padding:25px } td.es-m-p25t { padding-top:25px } td.es-m-p25b { padding-bottom:25px } td.es-m-p25r { padding-right:25px } td.es-m-p25l { padding-left:25px } td.es-m-p30 { padding:30px } td.es-m-p30t { padding-top:30px } td.es-m-p30b { padding-bottom:30px } td.es-m-p30r { padding-right:30px } td.es-m-p30l { padding-left:30px } td.es-m-p35 { padding:35px } td.es-m-p35t { padding-top:35px } td.es-m-p35b { padding-bottom:35px } td.es-m-p35r { padding-right:35px } td.es-m-p35l { padding-left:35px } td.es-m-p40 { padding:40px } td.es-m-p40t { padding-top:40px } td.es-m-p40b { padding-bottom:40px } td.es-m-p40r { padding-right:40px } td.es-m-p40l { padding-left:40px } .es-desk-hidden { display:table-row; width:auto; overflow:visible; max-height:inherit } }</style></head>\n" +
                    "<body><div class=\"es-wrapper-color\"> <!--[if gte mso 9]><v:background xmlns:v=\"urn:schemas-microsoft-com:vml\" fill=\"t\"> <v:fill type=\"tile\" color=\"#fafafa\"></v:fill> </v:background><![endif]--><table class=\"es-wrapper\" width=\"100%\" cellspacing=\"0\" cellpadding=\"0\"><tr><td valign=\"top\"><table cellpadding=\"0\" cellspacing=\"0\" class=\"es-header\" align=\"center\"><tr><td align=\"center\"><table bgcolor=\"#ffffff\" class=\"es-header-body\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\"><tr><td class=\"es-p10t es-p10b es-p20r es-p20l\" align=\"left\"><table cellpadding=\"0\" cellspacing=\"0\" width=\"100%\"><tr><td width=\"560\" class=\"es-m-p0r\" valign=\"top\" align=\"center\"><table cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" role=\"presentation\"><tr><td align=\"center\" style=\"font-size: 0px\"><amp-img class=\"adapt-img\" src=\"https://qxvvzs.stripocdn.email/content/guids/CABINET_982defb57c54e7bfffcb800734d15f949a970475d9e1a87b3f8af0f856c07891/images/logos_turing_parking05.png\" alt style=\"display: block\" height=\"126\" width=\"126\" layout=\"responsive\"></amp-img></td>\n" +
                    "</tr><tr><td align=\"center\" style=\"font-size: 0px\"><amp-img class=\"adapt-img\" src=\"https://qxvvzs.stripocdn.email/content/guids/CABINET_982defb57c54e7bfffcb800734d15f949a970475d9e1a87b3f8af0f856c07891/images/logos_turing_parking06.png\" alt style=\"display: block\" height=\"27\" width=\"314\" layout=\"responsive\"></amp-img></td></tr></table></td></tr></table></td></tr></table></td>\n" +
                    "</tr></table><table cellpadding=\"0\" cellspacing=\"0\" class=\"es-content\" align=\"center\"><tr><td align=\"center\"><table bgcolor=\"#ffffff\" class=\"es-content-body\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\"><tr><td class=\"es-p30t es-p10b es-p20r es-p20l\" align=\"left\"><table cellpadding=\"0\" cellspacing=\"0\" width=\"100%\"><tr><td width=\"560\" align=\"center\" valign=\"top\"><table cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" style=\"border-radius: 3px;border-collapse: separate\" role=\"presentation\"><tr><td align=\"center\" class=\"es-p10t es-p10b\" style=\"font-size: 0px\"><amp-img src=\"https://qxvvzs.stripocdn.email/content/guids/CABINET_a3448362093fd4087f87ff42df4565c1/images/78501618239341906.png\" alt style=\"display: block\" width=\"100\" height=\"72\"></amp-img></td></tr><tr><td align=\"center\" class=\"es-p10b es-m-txt-c\"><h1 style=\"font-size: 46px;line-height: 46px;color: #0060ab\">Olá, seja bem vindo à Turing Parking</h1></td>\n" +
                    "</tr><tr><td align=\"center\" class=\"es-p5t es-p5b es-p40r es-p40l es-m-p0r es-m-p0l\"><p>Para confirmar seu cadastro, por favor informe o código abaixo, ele somente ficará válido por 5 minutos.</p></td></tr></table></td></tr></table></td></tr><tr><td class=\"es-p10t es-p10b es-p20r es-p20l\" align=\"left\" bgcolor=\"#0060ab\" style=\"background-color: #0060ab;border-radius: 15px\"><table cellpadding=\"0\" cellspacing=\"0\" width=\"100%\"><tr><td width=\"560\" align=\"center\" valign=\"top\"><table cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" style=\"border-radius: 30px;border-collapse: separate;background-color: #0060ab\" bgcolor=\"#0060ab\" role=\"presentation\"><tr><td align=\"center\" class=\"es-m-txt-c es-p20t es-p20r es-p20l\" bgcolor=\"#0060ab\"><h2 style=\"color: #d3e3ff;line-height: 100%\">Código de confirmação</h2></td></tr><tr><td align=\"center\" class=\"es-m-txt-c es-p20b\" bgcolor=\"#0060ab\"><h1 style=\"color: #10fbfd\"><strong>${code}</strong></h1></td>\n" +
                    "</tr></table></td></tr></table></td></tr></table></td></tr></table><table cellpadding=\"0\" cellspacing=\"0\" class=\"es-footer\" align=\"center\"><tr><td align=\"center\"><table class=\"es-footer-body\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" width=\"640\" style=\"background-color: transparent\"><tr><td class=\"es-p20t es-p20b es-p20r es-p20l\" align=\"left\"><table cellpadding=\"0\" cellspacing=\"0\" width=\"100%\"><tr><td width=\"600\" align=\"left\"><table cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" role=\"presentation\"><tr><td align=\"right\" style=\"font-size: 0px\"><amp-img class=\"adapt-img\" src=\"https://qxvvzs.stripocdn.email/content/guids/CABINET_982defb57c54e7bfffcb800734d15f949a970475d9e1a87b3f8af0f856c07891/images/logos_turing_parking07.png\" alt style=\"display: block\" height=\"40\" width=\"160\" layout=\"responsive\"></amp-img></td></tr></table></td></tr></table></td></tr></table></td></tr></table></td></tr></table></div></body></html>"
        }

        fun postMailUsingVolley(email: String, code: String, context: Context) {
            // on below line specifying the url at which we have to make a post request
            val apiKey = BuildConfig.BREVO_API_KEY
            val url = "https://api.brevo.com/v3/smtp/email"
            // creating a new variable for our request queue
            val queue = Volley.newRequestQueue(context)
            // making a string request on below line.
            val request: StringRequest =
                object : StringRequest(Method.POST, url, Response.Listener {
                    Toast.makeText(
                        context,
                        "Email enviado!",
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.d(TAG, "Email enviado")
                }, Response.ErrorListener { error -> // handling error on below line.
                    Toast.makeText(context, "Falha no envio do email!", Toast.LENGTH_SHORT)
                        .show()
                    Log.d(TAG, "postMailUsingVolley: $error")
                }) {

                    @Throws(AuthFailureError::class)
                    override fun getBody(): ByteArray {
                        val gson = Gson()
                        val sender = Sender("Turing Team","no-reply@turing.com.br")
                        val to = To(email, "nome")
                        val toArray = listOf(to)
                        val subject = "Verify Code"
                        val htmlContent = setCodeHtml(code)
                        val bodyJson = gson.toJson(Body(sender, toArray, subject, htmlContent))
                        return bodyJson.toString().encodeToByteArray()
                    }

                    @Throws(AuthFailureError::class)
                    override fun getHeaders(): Map<String, String> {
                        val headers = hashMapOf<String, String>()
                        headers["content-type"] = "application/json"
                        headers["accept"] = "application/json"
                        headers["api-key"] = apiKey
                        return headers
                    }
                }
            queue.add(request)
        }
    }

}