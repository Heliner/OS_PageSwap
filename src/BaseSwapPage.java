import java.util.List;
import java.util.Random;

public abstract class BaseSwapPage  implements Cloneable{

    public int lostPage;
    public int serialize;
    public int memPagesSize;

    public abstract List<Integer> cachedPage();

    public abstract void init(int memPagesSize);


    public void swapOne(Integer pageNum) {
        System.out.println("");
        System.out.println("正在请求置换页面:" + pageNum);
        serialize++;
        selectFIPage(pageNum);
        System.out.println("页面置换完成");
        System.out.println();
    }

    public abstract void selectFIPage(Integer pageNum);
    public abstract void printAllMemPage();
    public void printLostPageAndRatio() {
        System.out.println();
        System.out.println("缺页数:" + this.lostPage);
        System.out.println("缺页率:" + (1f * this.lostPage / this.serialize * 100 + "%"));
        System.out.println();
    }

    public void randomPageNumAndSwap() {
        System.out.println("正在随机生成页面号");
        Random random = new Random();
        int rand = random.nextInt(this.memPagesSize);
        System.out.println("生成页面号为 :" + rand);
        swapOne(rand);
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        BaseSwapPage o = null;
        try {
            o = (BaseSwapPage) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return o;
    }




}
