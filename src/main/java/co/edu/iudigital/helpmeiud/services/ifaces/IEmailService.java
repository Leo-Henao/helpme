package co.edu.iudigital.helpmeiud.services.ifaces;

public interface IEmailService {

    boolean sendMail(String mensaje, String email, String asunto);
}
