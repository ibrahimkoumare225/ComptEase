package fr.koumare.comptease.utilis;
import fr.koumare.comptease.annotations.Transactional;

import java.lang.reflect.Method;

public class TransactionInterceptor {

    public static void execute(Object object, Method method, Object[] args) throws Exception {
        if (method.isAnnotationPresent(Transactional.class)) {
            SessionManager.executeTransaction(session -> {
                try {
                    method.invoke(object, args);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        } else {
            method.invoke(object, args);  // Si pas annoté, juste exécuter normalement
        }
    }
}
