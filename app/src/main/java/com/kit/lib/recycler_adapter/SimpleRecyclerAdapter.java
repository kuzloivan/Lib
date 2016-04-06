package com.kit.lib.recycler_adapter;

import android.support.annotation.LayoutRes;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by ivan.k on 04.03.2016.
 */
public class SimpleRecyclerAdapter<T> extends RecyclerView.Adapter<SimpleRecyclerAdapter.BaseViewHolder> {

    private Class viewHolderClass;
    private int layoutId;
    private LayoutInflater layoutInflater;
    private List<T> data;
    private Clicker clicker;
    private OnItemClickListener<T> itemClickListener;
    private OnLongItemClickListener<T> longClickListener;

    public SimpleRecyclerAdapter(Class viewHolderClass, @LayoutRes int layoutId) {
        if(!BaseViewHolder.class.isAssignableFrom(viewHolderClass)){
            throw new IllegalArgumentException("viewHolderClass must be implementation of BaseViewHolder");
        }
        this.viewHolderClass = viewHolderClass;
        this.layoutId = layoutId;
        data = new ArrayList<>();
        clicker = new Clicker();
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return newViewHolder(parent, viewType);
    }

    private BaseViewHolder newViewHolder(ViewGroup parent, int viewType) {
        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(parent.getContext());
        }

        View itemView = layoutInflater.inflate(layoutId, parent, false);
        itemView.setOnClickListener(clicker);
        itemView.setOnLongClickListener(clicker);
        return createVH(getViewHolderClassByType(viewType), itemView);
    }

    public static BaseViewHolder createVH(Class c, View view) {
        try {
            return (BaseViewHolder) c.getDeclaredConstructors()[0].newInstance(view);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        holder.fill(data.get(position));
        holder.itemView.setTag(position);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void clear() {
        data.clear();
        notifyDataSetChanged();
    }

    public void add(T item) {
        data.add(item);
        notifyDataSetChanged();
    }

    public void addAll(Collection<T> items) {
        data.addAll(items);
        notifyDataSetChanged();
    }

    public T getItem(int position) {
        return data.get(position);
    }


    public Class getViewHolderClassByType(int viewType){
        return viewHolderClass;
    }

    public void setItemClickListener(OnItemClickListener<T> itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public void setLongClickListener(OnLongItemClickListener<T> longClickListener) {
        this.longClickListener = longClickListener;
    }

    private class Clicker implements View.OnClickListener, View.OnLongClickListener{

        @Override
        public void onClick(View v) {
            if (itemClickListener != null) {
                Integer position = (Integer) v.getTag();
                itemClickListener.onItemClicked(getItem(position), position);
            }
        }

        @Override
        public boolean onLongClick(View v) {
            if (longClickListener != null) {
                Integer position = (Integer) v.getTag();
                longClickListener.onLongClick(getItem(position), position);
                return true;
            }
            return false;
        }
    }


    public interface OnItemClickListener<T> {
        void onItemClicked(T item, int position);
    }
    public interface OnLongItemClickListener<T> {
        void onLongClick(T item, int position);
    }
    public static abstract class BaseViewHolder<T> extends RecyclerView.ViewHolder {

        public BaseViewHolder(View itemView) {
            super(itemView);
        }

        public abstract void fill(T object);
    }


}
