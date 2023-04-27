package zerobase.weather.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import zerobase.weather.domain.Diary;
import zerobase.weather.service.DiaryService;

@RestController
public class DiaryController {
	
	private final DiaryService diaryService;
	
	public DiaryController(DiaryService diaryService) {
		this.diaryService = diaryService;
	}
	
	@Operation(description="일기 텍스트와 날씨를 이용해서 DB에 일기 저장")
	@PostMapping("/create/diary")
	void createDiary(
			@RequestParam 
			@DateTimeFormat(iso = DateTimeFormat.ISO.DATE) 
			LocalDate date,
			@RequestBody String text
	) {
		diaryService.createDiary(date, text);
	}
	
	@Operation(description="선택한 날짜의 모든 일기 데이터를 가져옵니다")
	@GetMapping("/read/diary")
	List<Diary> readDiary(
			@RequestParam 
			@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
			LocalDate date
	) {
		return diaryService.readDiary(date);
	}
	
	@Operation(description="선택한 기간의 모든 일기 데이터를 가져옵니다")
	@GetMapping("/read/diaries")
	List<Diary> readDiaries(
			@RequestParam 
			@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
			LocalDate startDate,
			@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
			LocalDate endDate
	) {
		return diaryService.readDiaries(startDate, endDate);
	}
	
	@Operation(description="선택한 기간의 처음 일기를 수정합니다")
	@PutMapping("/update/diary")
	void updateDiary(
			@RequestParam 
			@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
			LocalDate date,
			@RequestBody
			String text
		) {
		diaryService.updateDiary(date, text);
	}
	
	@Operation(description="선택한 기간의 일기 데이터를 삭제합니다")
	@DeleteMapping("/delete/diary")
	void deleteDiary(
			@RequestParam 
			@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
			LocalDate date
			) {
		diaryService.deleteDiary(date);
	}
	
}
