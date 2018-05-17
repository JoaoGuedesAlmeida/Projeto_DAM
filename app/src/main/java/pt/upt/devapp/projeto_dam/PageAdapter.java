package pt.upt.devapp.projeto_dam;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

//classe que controla os fragmentos da nossa main activity
//feita com a ajuda do seguinte vídeo: https://www.youtube.com/watch?v=00LLd7qr9sA
class PageAdapter extends FragmentPagerAdapter{


    public PageAdapter(FragmentManager fm) {
        super(fm);
    }

    //método que permite a troca entre fragmentos na main activity
    @Override
    public Fragment getItem(int position) {
        switch(position){
            case 0:
                RequestsF requestsF = new RequestsF();
                return requestsF;

            case 1:
                ChatF chatF = new ChatF();
                return chatF;

            case 2:
                FriendsF friendsF = new FriendsF();
                return friendsF;

            default:
                return null;
        }
    }

    //método para colocar os nomes nas tabs
    public CharSequence getPageTitle(int position){
        switch(position){
            case 0:
                return "REQUESTS";

            case 1:
                return "CHATS";

            case 2:
                return "FRIENDS";

            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 3;
    }

}
