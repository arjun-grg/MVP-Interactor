package com.wongel.MVPGenerator;

/**
 * Created by tseringwongelgurung on 12/27/17.
 */

class MvpModule {
    private String name;
    private boolean isFragment;
    private boolean hasInteractor;
    private boolean isKotlin;
    private MVP_TYPE mosbyType;

    public MvpModule(String name, boolean isFragment, boolean hasInteractor, boolean isKotlin, MVP_TYPE mosbyType) {
        this.name = name;
        this.isFragment = isFragment;
        this.hasInteractor = hasInteractor;
        this.isKotlin = isKotlin;
        this.mosbyType = mosbyType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isFragment() {
        return isFragment;
    }

    public void setFragment(boolean fragment) {
        isFragment = fragment;
    }

    public boolean hasInteractor() {
        return hasInteractor;
    }

    public void setHasInteractor(boolean hasInteractor) {
        this.hasInteractor = hasInteractor;
    }

    public boolean isKotlin() {
        return isKotlin;
    }

    public void setKotlin(boolean kotlin) {
        isKotlin = kotlin;
    }

    public MVP_TYPE getMosbyType() {
        return mosbyType;
    }

    public void setMosbyType(MVP_TYPE mosbyType) {
        this.mosbyType = mosbyType;
    }

    enum MVP_TYPE {None, mosby1, mosby3}
}
