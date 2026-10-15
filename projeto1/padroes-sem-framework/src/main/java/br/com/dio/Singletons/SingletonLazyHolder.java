package br.com.dio.Singletons;
//Singleton preguiçoso//
public class SingletonLazyHolder {
    private static SingletonLazyHolder instancia;

    private static class InstanceHolder{
        public static SingletonLazyHolder instancia = new SingletonLazyHolder();
    }
    private SingletonLazyHolder(){
        super();
    }

    public static SingletonLazyHolder getInstance(){
        return InstanceHolder.instancia;
    }
}

