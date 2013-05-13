package com.qrobot.mm.pet;

import android.os.Parcel;
import android.os.Parcelable;

public final class Pet implements Parcelable {

	// ���л���Parcelable�ӿ�

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


	// ��Ӧ�Ĺ�����ÿһ�е�ӳ��
	public int id;
	public String nickname;
	public String portrait;
	public int nicklevel; //�ȼ�

	public Pet(Parcel p) {
		id = p.readInt();
		nickname = p.readString();
		portrait = p.readString();
		nicklevel = p.readInt();
	}

	// ����һ��Ĭ������
	public Pet() {
		id = -1;
		nickname="xiao Q";
		portrait = "default";
		nicklevel = 1;
	}

}
