import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class OPTSwap {
    /*用于显示当前缓存中的页面*/
    List<Integer> memPageCache = new ArrayList<>();
    /*指向当前需要判断的页面*/
    int memPagePointer = -1;
    /*全部页面*/
    int[] totalPage;
    //缓存大小
    int memPagesSize;
    //用于记录当前页块的版本号
    int serialize = 0;
    //缺页个数
    int lostPage = 0;
    List<Integer> lastOccurent = new ArrayList<>();
    static final int NOT_OCC_YET = 0;
    static final int FREE_PAGE = -1;

    void init(int memPagesSize, int[] totalPage) {
        memPageCache.clear();
        lastOccurent.clear();

        Scanner sc = new Scanner(System.in);
        if (memPagesSize <= 0)
            return;

        this.memPagesSize = memPagesSize;
        lostPage = 0;
        serialize = 0;
        this.totalPage = totalPage;

        for (int i = 0; i < memPagesSize; i++) {
            memPageCache.add(FREE_PAGE);
            /*lastOccurent.add(NOT_OCC_YET);*/
        }
    }

    //选择策略
    private int selectFIPage(int newMemPageNum) {
        /*有空闲分区*/
        memPagePointer++;
        if (memPageCache.contains(newMemPageNum)) {
            /*什么都不做*/
            System.out.println();
            System.out.println("当前分区已存在");
            System.out.println();
        } else /*更新往后最近出现的时机*/ {
            /*当前缓存中存在该值*/
            if (memPageCache.contains(FREE_PAGE)) {
                lostPage++;
                memPageCache.set(memPageCache.indexOf(FREE_PAGE), newMemPageNum);
                System.out.println("存在空闲分区 直接进行置换");
            } else/*当前缓存不包含对应的值*/ {
                /*统计出现出现的位置*/
                lostPage++;
                lastOccurent.clear();
                for (int i = memPagePointer; i < totalPage.length; i++) {
                    boolean flag = false;
                    int tCurMemPage = -1;
                    for (int curMemPageCache : memPageCache) {
                        if (curMemPageCache == totalPage[i]) {
                            flag = true;
                            tCurMemPage = curMemPageCache;
                            break;
                        }
                    }
                    if (flag == true&&!lastOccurent.contains(tCurMemPage)) {
                        lastOccurent.add(tCurMemPage);
                    }
                    if (lastOccurent.size() == memPagesSize) {
                        break;
                    }
                }

                int replacePageNum = 0;
                /*缓存中的页面在以后均不会出现*/
                if (lastOccurent.size() == 0) {
                    replacePageNum = memPageCache.get(0);
                } else if (lastOccurent.size() < memPagesSize)/*缓存中的页面有一部分在后面不会出现*/{
                    List<Integer> copyMemPage = new ArrayList<>();
                    /*找出两个集合相异的元素也就是未出现的元素*/
                    copyMemPage.addAll(memPageCache);
                    copyMemPage.removeAll(lastOccurent);
                    replacePageNum = copyMemPage.get(0);
                } else/*全部页面都存在*/{
                    /*置换最后一个出现的页面*/
                    replacePageNum = lastOccurent.get(lastOccurent.size() - 1);
                }
                if (memPageCache.contains(replacePageNum)) {
                    memPageCache.set(memPageCache.indexOf(replacePageNum), newMemPageNum);
                    System.out.println("置换页面 原本页面号:" + replacePageNum + " ; 现页面号:" + newMemPageNum);
                } else {
                    System.out.println("错误----------------");
                }
            }
        }
        return -1;
    }

    @Test
    public void automaticRun() {
        init(3, new int[]{7, 0, 1, 2, 0, 3, 0, 4, 2, 3});
        for (int pageNum : totalPage) {
            swapOne(pageNum);
        }
        printLostPageAndRatio();

    }

    private void swapOne(int memPageNum) {
        System.out.println("");
        System.out.println("正在请求置换页面:" + memPageNum);
        serialize++;
        selectFIPage(memPageNum);
        System.out.println("页面置换完成");
        System.out.println();
        this.printAllMemPage();
    }

    public void printAllMemPage() {
        System.out.println();
        for (int i = 0; i < memPageCache.size(); i++) {
            System.out.print(memPageCache.get(i) +" ");
        }
        System.out.println();
    }


    public void choseAndInit() {
        Scanner sc = new Scanner(System.in);
        System.out.println("input the page size :");
        int pageSize = sc.nextInt();
        init(pageSize, new int[]{7, 0, 1, 2, 0, 3, 0, 4, 2, 3});
    }

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
}
