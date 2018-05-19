package pt.upt.devapp.projeto_dam;

public class Friend {
    private String data;

    public Friend(String data){
        this.data = data;
    }

    public Friend(){};

    public void setData(String data){
        this.data = data;
    }

    public String getData(){
        return data;
    }
}
