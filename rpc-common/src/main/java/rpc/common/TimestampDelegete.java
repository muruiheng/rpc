package rpc.common;

import java.io.IOException;
import java.sql.Timestamp;

import com.dyuproject.protostuff.Input;
import com.dyuproject.protostuff.Output;
import com.dyuproject.protostuff.Pipe;
import com.dyuproject.protostuff.WireFormat.FieldType;
import com.dyuproject.protostuff.runtime.Delegate;

public class TimestampDelegete implements Delegate<Timestamp> {

	@Override
	public FieldType getFieldType() {
		// TODO Auto-generated method stub
		return FieldType.FIXED64;
	}

	@Override
	public Timestamp readFrom(Input input) throws IOException {
		// TODO Auto-generated method stub
		return new Timestamp(input.readFixed64());
	}

	@Override
	public void writeTo(Output output, int number, Timestamp value, boolean repeated) throws IOException {
		output.writeFixed64(number, value.getTime(), repeated);
	}

	@Override
	public void transfer(Pipe pipe, Input input, Output output, int number, boolean repeated) throws IOException {
		// TODO Auto-generated method stub
		output.writeFixed64(number, input.readFixed64(), repeated);
	}

	@Override
	public Class<?> typeClass() {
		// TODO Auto-generated method stub
		return Timestamp.class;
	}

}
