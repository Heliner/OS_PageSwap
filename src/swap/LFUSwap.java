package swap;

import base.BaseSwapPage;
import org.junit.Test;

import java.util.*;

public class LFUSwap extends BaseSwapPage implements Cloneable {
    LinkedList<Page> lfuQueue = new LinkedList<>();
    Page EMPTY_PAGE = new Page(-1,serialize);

    @Override
    public void init(int memPagesSize) {
        if (memPagesSize <= 0)
            return;
        lfuQueue.clear();
        this.memPagesSize = memPagesSize;
        lostPage = 0;
        serialize = 0;
        for (int i = 0; i < memPagesSize; i++) {
            lfuQueue.add(EMPTY_PAGE);
        }
    }

    //选择策略
    @Override
    public void selectFIPage(Integer memPageNum) {
        Page insertPage = new Page(memPageNum, this.serialize);
        /*替换空闲空间*/
        if (lfuQueue.contains(insertPage)) {
            int index = lfuQueue.indexOf(insertPage);
            Page oldPage = lfuQueue.get(index);
            oldPage.frequent++;
            /*按频率进行排序*/
            System.out.println("0" + lfuQueue.get(0).frequent);
            System.out.println("1" + lfuQueue.get(1).frequent);
        } else {
            /*如果包含此元素频率增加，并且进行排序*/
            if (lfuQueue.contains(EMPTY_PAGE)) {
                lostPage++;
                lfuQueue.set(lfuQueue.indexOf(EMPTY_PAGE), insertPage);
                System.out.println("置换空闲页面");
            } else/*移除队尾元素*/ {
                /*移除最后一个元素,然后插入信息的页面*/
                List<Page> sortList = new ArrayList<>();
                sortList.addAll(lfuQueue);
                Collections.sort(sortList, new PageComparator());
                Page oldPage = lfuQueue.get(lfuQueue.indexOf(sortList.get(sortList.size() - 1)));
                lfuQueue.set(lfuQueue.indexOf(sortList.get(sortList.size() - 1)), insertPage);
                System.out.println("需要进行页面置换，：原页面索引：" + (memPagesSize - 1) + " 编号：" + oldPage.pageNum + " 频率:" + oldPage.frequent);

            }
        }
    }

    @Test
    public void automaticRun() {
        init(3);
        swapOne(7);
        swapOne(0);
        swapOne(1);
        swapOne(2);
        swapOne(0);
        swapOne(3);
        swapOne(0);
        swapOne(4);
        swapOne(2);
        swapOne(3);
        swapOne(0);
        swapOne(3);
        swapOne(2);
        swapOne(1);
        swapOne(2);
        swapOne(0);
        swapOne(1);
        swapOne(7);
        swapOne(0);
        swapOne(1);
        printLostPageAndRatio();
    }

    public void choseAndInit() {
        Scanner sc = new Scanner(System.in);
        System.out.println("input the page size :");
        int pageSize = sc.nextInt();
        init(pageSize);
    }

    @Override
    public void printAllMemPage() {
        System.out.println();
        for (int i = 0; i < lfuQueue.size(); i++) {
            System.out.println("index " + i + "  pageNum : " + lfuQueue.get(i));
        }
        System.out.println();
    }


    static class Page {
        int frequent = 0;
        int pageNum = 0;
        int serialize = 0;

        public Page(int pageNum, int serialize) {
            this.pageNum = pageNum;
            this.frequent = 1;
            this.serialize = serialize;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null || !(obj instanceof Page))
                return false;
            return this.pageNum == ((Page) obj).pageNum;
        }
    }

    class PageComparator implements Comparator<Page> {
        @Override
        public int compare(Page o1, Page o2) {
            if (o1.frequent == o2.frequent)
                return -(o1.serialize - o2.serialize);
            return -(o1.frequent - o2.frequent);
        }

        @Override
        public boolean equals(Object obj) {
            return false;
        }
    }


    @Override
    public List<Integer> cachedPage() {
        List<Integer> lfuQueueList = new LinkedList<>();
        for (Page page : lfuQueue) {
            lfuQueueList.add(page.pageNum);
        }
        return lfuQueueList;
    }


}
