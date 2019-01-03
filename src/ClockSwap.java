import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ClockSwap extends BaseSwapPage {
    private static final boolean ACCESSED = true;
    private static final boolean NOT_ACCESSED = false;
    List<Page> clockDequeList = new ArrayList<>();

    static final Page FREE_PAGE = new Page(-1);
    int lastReplaceIndex = -1;

    @Override
    public List<Integer> cachedPage() {
        List<Integer> re = new ArrayList<>();
        for (Page page : clockDequeList) {
            re.add(page.pageNum);
        }
        return re;
    }

    public void init(int memPagesSize) {
        if (memPagesSize <= 0)
            return;
        this.lastReplaceIndex = -1;
        this.memPagesSize = memPagesSize;
        lostPage = 0;
        serialize = 0;
        clockDequeList.clear();
        for (int i = 0; i < memPagesSize; i++) {
            clockDequeList.add(FREE_PAGE);
        }
    }


    //选择策略
    @Override
    public void selectFIPage(Integer memPageNum) {
        if (memPageNum < 0)
            return;
        /*包含-1*/
        Page pageNew = new Page(memPageNum);
        /*当前分区存在,不改变久指针的位置*/
        if (clockDequeList.contains(pageNew)) {
            System.out.println("分区号为:" + memPageNum + " 的分区已存在，改变标志位为1");
            Page oldPage = clockDequeList.get(clockDequeList.indexOf(pageNew));
            oldPage.isAccessed = ACCESSED;
        } else {
            int replaceIndex = -1;
            if (clockDequeList.contains(FREE_PAGE)) {
                lostPage++;
                int freeIndex = clockDequeList.indexOf(FREE_PAGE);

                System.out.println("当前页面存在空闲分区 : 分区索引：" + freeIndex);
                clockDequeList.set(freeIndex, pageNew);
                lastReplaceIndex++;
                System.out.println("置换空闲分区:" + freeIndex + "结束");
            } else {
                lostPage++;
                System.out.println("开始遍历寻找空闲的分区");
                int oneRound = this.memPagesSize + 1;
                while (oneRound > 0) {
                    lastReplaceIndex++;
                    lastReplaceIndex %= memPagesSize;

                    Page curPage = clockDequeList.get(lastReplaceIndex);
                    if (curPage.isAccessed == ACCESSED) {
                        curPage.isAccessed = NOT_ACCESSED;
                        System.out.println("设置页面:" + curPage.pageNum + " 状态为：未访问");
                    } else {
                        replaceIndex = lastReplaceIndex;
                        clockDequeList.set(replaceIndex, pageNew);
                        System.out.println("替换索引:" + replaceIndex + " 原来值为:" + curPage.pageNum + " 为:" + pageNew.pageNum);
                        break;
                    }
                    oneRound--;
                }

            }

            if (replaceIndex == -1) {
                System.out.println("出现错误");
            }
        }
    }

    @Test
    public void automaticRun() {
        init(5);
        swapOne(1);
        swapOne(3);
        swapOne(4);
        swapOne(2);
        swapOne(5);
        swapOne(6);
        swapOne(3);
        swapOne(4);
        swapOne(7);

        printLostPageAndRatio();
    }

    @Deprecated
    public void choseAndInit() {
        Scanner sc = new Scanner(System.in);
        System.out.println("input the page size :");
        int pageSize = sc.nextInt();
        init(pageSize);
    }

    public void printAllMemPage() {
        System.out.println();
        for (int i = 0; i < clockDequeList.size(); i++) {
            System.out.println("当前页面索引：" + i + " 值为:" + clockDequeList.get(i).pageNum + " 被访问：" + clockDequeList.get(i).isAccessed);
        }
        System.out.println();

    }

    static class Page {
        int pageNum;
        boolean isAccessed = ACCESSED;

        public Page(int pageNum) {
            this.pageNum = pageNum;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null || !(obj instanceof Page))
                return false;
            return this.pageNum == ((Page) obj).pageNum;
        }
    }
}
