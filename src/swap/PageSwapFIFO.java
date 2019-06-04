package swap;

import base.BaseSwapPage;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class PageSwapFIFO extends BaseSwapPage {
    List<Integer> memPages = new ArrayList<>();
    final Integer EMPTY_PAGE = -1;
    Integer curPointer = 0;

    @Override
    public List<Integer> cachedPage() {
        return memPages;
    }

    @Override
    public void init(int memPagesSize) {
        if (memPagesSize <= 0)
            return;
        this.memPagesSize = memPagesSize;
        memPages.clear();
        lostPage = 0;
        serialize = 0;
        curPointer = 0;
        for (int i = 0; i < memPagesSize; i++) {
            memPages.add(EMPTY_PAGE);
        }
    }


    //选择策略
    @Override
    public void selectFIPage(Integer memPageNum) {
        /*当前页面存在*/
        if (memPageNum < 0)
            return;
        if (memPages.contains(memPageNum)) {
            /*什么都不做*/
            System.out.println();
            System.out.println("当前页面存在");
            System.out.println();
        } else {
            lostPage++;
            /*包含空闲页面*/
            if (memPages.contains(EMPTY_PAGE)) {
                System.out.println("当前页面存在空闲分页");
                int freeIndex = memPages.indexOf(EMPTY_PAGE);
                System.out.println("请求置换索引：" + freeIndex + " 序号为:" + memPages.get(freeIndex));
                memPages.set(freeIndex, memPageNum);
                System.out.println();
            } else /*不包含空闲分区*/ {
                System.out.println("请求置换页面索引:" + curPointer + " ；当前值为:" + memPages.get(curPointer));
                memPages.set(curPointer, memPageNum);
            }
            curPointer++;
            curPointer %= memPagesSize;
        }
    }

    //置换
    @Override
    public void swapOne(Integer memPageNum) {
        System.out.println("");
        System.out.println("正在请求置换页面:" + memPageNum);
        serialize++;
        selectFIPage(memPageNum);
        System.out.println("页面置换完成");
        System.out.println();
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

    @Deprecated
    public void choseAndInit() {
        Scanner sc = new Scanner(System.in);
        System.out.println("input the page size :");
        int pageSize = sc.nextInt();
        init(pageSize);
    }

    public void printAllMemPage() {
        System.out.println();
        for (int i = 0; i < memPages.size(); i++) {
            if (memPages.get(i) != -1) {
                System.out.println("index " + i + "  pageNum : " + memPages.get(i));
            }
        }
        System.out.println();
    }


    public void printLostPageAndRatio() {
        System.out.println();
        System.out.println("缺页数:" + this.lostPage);
        System.out.println("缺页率:" + (1f * this.lostPage / this.serialize * 100 + "%"));
        System.out.println();
    }

}
