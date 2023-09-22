package com.example.turingparking.network

import java.util.Properties
import javax.mail.Authenticator
import javax.mail.Message
import javax.mail.MessagingException
import javax.mail.PasswordAuthentication
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

class SendMail {

    private lateinit var session: Session
        fun init() {
            val username = "gustavotrevisandini@gmail.com"
            val password = "pSOZgmMa6LQdrHEv"
            val props = Properties()
            props.put("mail.smtp.starttls.enable", "true")
            props.put("mail.smtp.auth", "true")
            props.put("mail.smtp.host", "smtp-relay.sendinblue.com")
            props.put("mail.smtp.port", "587")
            session = Session.getInstance(props,
                object : Authenticator() {
                    override fun getPasswordAuthentication(): PasswordAuthentication {
                        return PasswordAuthentication(username, password)
                    }
                })
        }

    fun sendMail(to: String, content: String, subject: String){
        try {
            val message: Message = MimeMessage(session)
            message.setFrom(InternetAddress("turing.fiap@gmail.com"))
            message.setRecipients(
                Message.RecipientType.TO,
                InternetAddress.parse(to)
            )
            message.subject = subject
            message.setText(content)
            Transport.send(message)
            println("Done")
        } catch (e: MessagingException) {
            throw RuntimeException(e)
        }
    }
}