package com.qrobot.mm.pet;

import android.os.Parcel;
import android.os.Parcelable;

public final class Pet implements Parcelable {

	// 序列化的Parcelable接口

	public static final Parcelable.Creator<Pet> CREATOR = new Parcelable.Creator<Pet>() {
		@Override
		public Pet createFromParcel(Parcel p) {
			return new Pet(p);
		}

		@Override
		public Pet[] newArray(int size) {
			return new Pet[size];
		}
	};

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel p, int flags) {
		p.writeInt(id);
		p.writeString(nickname);
		p.writeString(portrait);
		p.writeInt(nicklevel);
	}


	// 对应的公共的每一列的映射
	public int id;
	public String nickname;
	public String portrait;
	public int nicklevel; //等级

	public Pet(Parcel p) {
		id = p.readInt();
		nickname = p.readString();
		portrait = p.readString();
		nicklevel = p.readInt();
	}

	// 创建一个默认属性
	public Pet() {
		id = -1;
		nickname="xiao Q";
		portrait = "default";
		nicklevel = 1;
	}

}
