import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Scanner;

public class ManageStudents {
	public static void main(String[] args) {

		if (args.length != 3  && args.length != 7) {
            System.out.println("USAGE: java ManageStudents   입력파일명  type [학생정보]");
            System.exit(0);
        }

        String inputFile = args[0];
        String type = args[1];
		String search = args[2];
        String changeName = null;
        String changeGender = null;
        String changePhoneNo = null;
        String changeAddress = null;
        int addressFoundCount = 0;
		File in_f = new File(inputFile);
        LinkedList<Long> availList = new LinkedList<>();
        ArrayList<String> numberList = new ArrayList<String>();
		File in_tf = new File(search);
        if (!in_f.exists()) {
			System.out.println(inputFile + " does not exist");
			System.exit(0);
		}
		if(!in_tf.exists() && type.equals("i")){
			System.out.println(search + " does not exist");
			System.exit(0);
		}

        if (type.equals("u")) {
            changeName = args[3];
            changeGender = args[4];
            changePhoneNo = args[5];
            changeAddress= args[6];
            if(!changeGender.equals("m") && !changeGender.equals("f") && !changeGender.equals("-")){
                System.out.println("변경될 성별이 m 또는 f 가 아닙니다.");
                System.exit(0);
			}
		}

		try {
		    RandomAccessFile din = new RandomAccessFile(in_f, "rw");
			Student s = new Student();
            int size;
			Boolean found = false;
            int recordNo =0;
            System.out.println("File Size : " + din.length());
            long startPointer = din.getFilePointer();
			while (true) {
				if ((size = s.readStudent(din)) < 0)
					break;
				
				
				s.number.contains("*");
				
                recordNo++;
				if (type.equals("p")) {

					if (search.equals("0")) {
                        found = true;
						if(s.number.contains("*")){
						    System.out.println("삭제된 레코드");
                        } else{
                            s.printStudent();
                        }
					}
					else {
                        found = true;
                        if (Integer.parseInt(search) == recordNo) {
							if (s.number.contains("*")) {
								System.out.println("[" +recordNo+ "] 삭제된 레코드 ");
							} else {
								s.printStudent();
							}
						}
					}
				} else if (type.equals("s")) {
					if (s.number.equals(search)) {
						found = true;
                        if(!s.number.contains("*")){
                           s.printStudent();
                        }
					}
				} else if (type.equals("n")) {
					if (s.name.equals(search)) {
						found = true;
                        if(!s.number.contains("*")){
                            s.printStudent();
                        } else{
                            found = false;
                        }
					}
				} else if(type.equals("a")) {
                    if(s.address.contains(search)){
                        found = true;
                        if(!s.number.contains("*")){
                            addressFoundCount++;
                            s.printStudent();
                        } else{
                            found = false;
                        }
                        if(addressFoundCount >0){
                            found = true;
                        }
                    }
                } else if (type.equals("d")) {
					if (search.equals("0")) {
						if (s.number.contains("*")) {
							found = true;
							System.out.println("[" + recordNo + "] 삭제된 공간 " + size + " 바이트 ");
						}
					} else {
						if (s.number.equals(search)) {
							found = true;
							din.seek(din.getFilePointer() - size);
                            availList.add(din.getFilePointer());
							din.writeBytes("*");
							din.seek(din.getFilePointer() + size);
							System.out.println(s.number + "학번이 삭제되었습니다.");
						}
					}
				}else if (type.equals("u")) {
                    Student newStudent = new Student();
					if (s.number.equals(search)) {
                        newStudent.number = s.number;
						found = true;
							if (!changeName.equals("-")) {
                                newStudent.name = changeName;
							} else {
                                newStudent.name = s.name;
							}
							if (!changeGender.equals("-")) {

                                newStudent.gender = changeGender;
							} else {
                                newStudent.gender = s.gender;
							}
							if (!changePhoneNo.equals("-")) {
                                newStudent.phone_no = changePhoneNo;
							} else {
                                newStudent.phone_no = s.phone_no;
							}

							if (!changeAddress.equals("-")) {
                                newStudent.address = changeAddress;
							} else {
                                newStudent.address = s.address;
							}
						int newSize = newStudent.toString().getBytes("utf-8").length;

						if (size < newSize) {
                            byte[] bytes = newStudent.toString().getBytes("utf-8");
                            s.printStudent();
                            din.seek(din.getFilePointer() - size);
							din.writeBytes("*");
							din.seek(din.length());
							newStudent.printStudent();
							din.writeInt(bytes.length);
							din.write(bytes);
						} else if(size == newSize){
                            byte[] bytes = newStudent.toString().getBytes("utf-8");
                            s.printStudent();
                            din.seek(din.getFilePointer() - size);
                            newStudent.printStudent();
                            din.write(bytes);
                        }
						else {
                            din.seek(din.getFilePointer() - size);
							byte[] bytes = newStudent.toString().getBytes("utf-8");
                            din.write(bytes);
							int nullCount = size - newSize;
							String nullSpace = "";
                            for (int j = 0; j < nullCount; j++) {
                                nullSpace += " ";
                            }
                            bytes = nullSpace.getBytes("utf-8");
                            din.write(bytes);
                            s.printStudent();
                            newStudent.printStudent();
						}
					}
				} else if(type.equals("i")) {
					
					if(!s.number.contains("*")) {
						numberList.add(s.number);
					}
					
					Student newStudent = new Student();
					FileReader fr = new FileReader(in_tf);
					BufferedReader br = new BufferedReader(fr);
					Scanner in = new Scanner(br);
					
					while(in.hasNext()){
						newStudent.getStudent(in);
						if(s.number.equals(newStudent.number)){
							int newSize = newStudent.toString().getBytes("utf-8").length;
							if (size < newSize) {
	                            long aa = din.getFilePointer();
								byte[] bytes = newStudent.toString().getBytes("utf-8");
	                            
	                            din.seek(din.getFilePointer() - size);
								din.writeBytes("*");
								din.seek(din.length());
								din.writeInt(bytes.length);
								din.write(bytes);
								din.seek(aa);
							} else if(size == newSize){
	                            byte[] bytes = newStudent.toString().getBytes("utf-8");
	                            din.seek(din.getFilePointer() - size);
	                            din.write(bytes);
	                        }
							else {
	                            din.seek(din.getFilePointer() - size);
								byte[] bytes = newStudent.toString().getBytes("utf-8");
	                            din.write(bytes);
								int nullCount = size - newSize;
								String nullSpace = "";
	                            for (int j = 0; j < nullCount; j++) {
	                                nullSpace += " ";
	                            }
	                            bytes = nullSpace.getBytes("utf-8");
	                            din.write(bytes);
							}
						}
					}
				}
				else{
				    System.out.println("No " + type );
				    System.exit(0);
                }
			}
			if(type.equals("i")) {
				found = true;
				Boolean numberCheck = false;
				Boolean textInsert = false;
				
				
				FileReader fr = new FileReader(in_tf);
				BufferedReader br = new BufferedReader(fr);
				Scanner in = new Scanner(br);
				Student newStudent = new Student();
				
				while(in.hasNext()){
					newStudent.getStudent(in);
					
					for(String check : numberList) {
						if(check.equals(newStudent.number)) {
							numberCheck = true;
						}
					}
					
					int newSize = newStudent.toString().getBytes("utf-8").length;
					
					if(numberCheck == false) {
						
						din.seek(startPointer);
						
						while(true) {
							if ((size = s.readStudent(din)) < 0)
								break;
							
							
							if(s.number.contains("*")) {
								if(size == newSize){
									textInsert= true;
		                            byte[] bytes = newStudent.toString().getBytes("utf-8");
		                            din.seek(din.getFilePointer() - size);
		                            
		                            din.write(bytes);
		                            break;
		                        }
								else if(size > newSize){
									textInsert= true;
		                            din.seek(din.getFilePointer() - size);
									byte[] bytes = newStudent.toString().getBytes("utf-8");
		                            din.write(bytes);
									int nullCount = size - newSize;
									String nullSpace = "";
		                            for (int j = 0; j < nullCount; j++) {
		                                nullSpace += " ";
		                            }
		                            bytes = nullSpace.getBytes("utf-8");
		                            din.write(bytes);
		                            break;
								}
							}
						}
						if(textInsert == false) {
							byte[] bytes = newStudent.toString().getBytes("utf-8");
							din.seek(din.length());
							
							din.writeInt(bytes.length);
							din.write(bytes);
						}
					}
					
					textInsert = false;
					numberCheck = false;
				}
				
			}
			if (search != null && !found)
				System.out.println("No " + search + "'s student exists");
			din.close(); // o
		} catch (IOException e) {
			System.out.println("file I/O error..");
		}
	}
}