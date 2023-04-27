package zerobase.weather.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import zerobase.weather.domain.Memo;


@SpringBootTest
@Transactional
class JpaMemoRepositoryTest {
	
	@Autowired
	JpaMemoRepository jpaMemoRepository;
	
	@Test
	void insertMemoTest(){
		//given
		Memo newMemo = new Memo(2, "this is jpa memo");
		//when
		jpaMemoRepository.save(newMemo);
		//then
		List<Memo> memoList = jpaMemoRepository.findAll();
		assertTrue(memoList.size()>0);
	}

}
