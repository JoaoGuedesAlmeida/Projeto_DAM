package pt.upt.devapp.projeto_dam;

public class Utilizador {

    public String nome;
    public String img;
    public String status;
    public String imgpeq;

    public Utilizador(String nome, String img, String status,String imgpeq){
        this.nome=nome;
        this.imgpeq = imgpeq;
        this.img = img;
        this.status = status;
    }

    public Utilizador(){};

    public String getNome(){
        return nome;
    }

    public String getImg(){
        return img;
    }

    public String getImgpeq(){
        return imgpeq;
    }

    public String getStatus(){
        return status;
    }

}
